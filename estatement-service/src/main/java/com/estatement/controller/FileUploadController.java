package com.estatement.controller;

import com.estatement.entity.FileUpload;
import com.estatement.dto.FileUploadDTO;
import com.estatement.entity.User;
import com.estatement.repository.FileUploadRepository;
import com.estatement.repository.UserRepository;
import com.estatement.service.FileStorageService;
import com.estatement.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final FileUploadRepository fileUploadRepository;
    private final UserRepository userRepository;
    private final FileProcessingService fileProcessingService;

    // REPLACE your existing uploadFile method with this one
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        Authentication authentication) {
        log.info("File upload request received: {}", file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            log.warn("Empty file upload attempt");
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "File is empty. Please upload a valid CSV file."));
        }

        // Check file type
        if (!isValidCsvFile(file)) {
            log.warn("Invalid file type: {}", file.getContentType());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid file type. Only CSV files are accepted."));
        }

        try {
            // Get current user
            String username = authentication.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            log.info("Processing file upload for user: {}", username);

            // Store file physically
            String storedFileName = fileStorageService.storeFile(file);
            log.info("File stored successfully: {}", storedFileName);

            // Create FileUpload entity
            FileUpload fileUpload = new FileUpload();
            fileUpload.setOriginalFilename(file.getOriginalFilename());
            fileUpload.setFilename(storedFileName);
            fileUpload.setFilePath("uploads/" + storedFileName);
            fileUpload.setUploadTime(LocalDateTime.now());
            fileUpload.setStatus("PROCESSING");
            fileUpload.setUploadedBy(currentUser);

            // Save initial record
            FileUpload savedFileUpload = fileUploadRepository.save(fileUpload);
            log.info("FileUpload entity saved with ID: {}", savedFileUpload.getId());

            // Process CSV file
            try {
                // Create a new input stream from the stored file on disk
                String filePath = "uploads/" + storedFileName;
                log.info("Reading file from disk: {}", filePath);

                InputStream fileInputStream = new FileInputStream(filePath);

                log.info("Starting CSV processing...");
                int recordCount = fileProcessingService.processAndInsertTransactions(
                        storedFileName,
                        fileInputStream,
                        savedFileUpload.getId()
                );

                // Close the input stream
                fileInputStream.close();

                // --- START OF NEW VALIDATION ---
                // Check if the service returned 0
                if (recordCount == 0) {
                    log.warn("File processing completed, but no new records were inserted (Upload ID: {})", savedFileUpload.getId());

                    // Update status to FAILED with a clear message
                    savedFileUpload.setStatus("FAILED");
                    savedFileUpload.setRecordCount(0);
                    savedFileUpload.setErrorMessage("No new or valid records were found in the file. All transactions may be duplicates or invalid.");
                    fileUploadRepository.save(savedFileUpload);

                    // Return a 400 Bad Request error to the frontend
                    return ResponseEntity.badRequest()
                            .body(Map.of("message", "Upload failed: No new or valid records were found in the file."));
                }
                // --- END OF NEW VALIDATION ---

                // If recordCount > 0, proceed with success
                savedFileUpload.setStatus("COMPLETED");
                savedFileUpload.setRecordCount(recordCount);
                fileUploadRepository.save(savedFileUpload);

                log.info("File processing completed. Records inserted: {}", recordCount);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "File uploaded and processed successfully");
                response.put("filename", file.getOriginalFilename());
                response.put("recordCount", recordCount);
                response.put("fileId", savedFileUpload.getId());

                return ResponseEntity.ok(response);

            } catch (Exception processingException) {
                log.error("Error processing CSV file: {}", processingException.getMessage(), processingException);
                processingException.printStackTrace();

                // Update file upload status to FAILED
                savedFileUpload.setStatus("FAILED");
                savedFileUpload.setErrorMessage(processingException.getMessage());
                fileUploadRepository.save(savedFileUpload);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "message", "File uploaded but processing failed",
                                "error", processingException.getMessage(),
                                "fileId", savedFileUpload.getId()
                        ));
            }

        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User authentication failed"));
        } catch (Exception e) {
            log.error("Unexpected error during file upload: {}", e.getMessage(), e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Unexpected error occurred during file upload", "error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<FileUploadDTO>> getUploadHistory(Authentication authentication) {
        log.info("Fetching upload history for user: {}", authentication.getName());

        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<FileUpload> uploads = fileUploadRepository.findByUploadedByOrderByUploadTimeDesc(currentUser);

        List<FileUploadDTO> uploadDTOs = uploads.stream()
                .map(upload -> {
                    FileUploadDTO dto = new FileUploadDTO();
                    dto.setId(upload.getId());
                    dto.setOriginalFilename(upload.getOriginalFilename());
                    dto.setUploadTime(upload.getUploadTime());
                    dto.setStatus(upload.getStatus());
                    dto.setRecordCount(upload.getRecordCount());
                    dto.setErrorMessage(upload.getErrorMessage());
                    return dto;
                })
                .collect(Collectors.toList());

        log.info("Returning {} upload records", uploadDTOs.size());
        return ResponseEntity.ok(uploadDTOs);
    }

    @GetMapping("/{fileId}/status")
    public ResponseEntity<?> getFileStatus(@PathVariable Long fileId, Authentication authentication) {
        log.info("Fetching status for file ID: {}", fileId);

        FileUpload fileUpload = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + fileId));

        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if user owns this file
        if (!fileUpload.getUploadedBy().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You do not have permission to access this file"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("fileId", fileUpload.getId());
        response.put("filename", fileUpload.getOriginalFilename());
        response.put("status", fileUpload.getStatus());
        response.put("recordCount", fileUpload.getRecordCount());
        response.put("uploadTime", fileUpload.getUploadTime());
        response.put("errorMessage", fileUpload.getErrorMessage());

        return ResponseEntity.ok(response);
    }

    /**
     * Validates if the uploaded file is a CSV file
     */
    private boolean isValidCsvFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        // Check file extension
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            return false;
        }

        // Check content type
        return contentType == null ||
                contentType.equals("text/csv") ||
                contentType.equals("application/vnd.ms-excel") ||
                contentType.equals("application/csv");
    }
}