package com.estatement.controller;

import com.estatement.dto.SearchCriteria;
import com.estatement.entity.Transaction;
import com.estatement.service.ExportService;
import jakarta.servlet.http.HttpServletResponse; // Use jakarta for Spring Boot 3+
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@Slf4j
public class ExportController {

    private final ExportService exportService;

    @PostMapping("/pdf")
    public void exportToPdf(@RequestBody SearchCriteria criteria, HttpServletResponse response) {
        log.info("Received PDF export request with criteria: {}", criteria);
        try {
            // Validate essential criteria
            if (criteria.getAccountNumber() == null || criteria.getStartDate() == null || criteria.getEndDate() == null) {
                log.warn("Export failed: Missing required criteria (accountNumber, startDate, endDate).");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"message\": \"Account Number, Start Date, and End Date are required for export.\"}");
                return;
            }

            // Generate PDF byte array
            byte[] pdfBytes = exportService.exportTransactionsToPdf(criteria);

            if (pdfBytes == null || pdfBytes.length == 0) {
                log.warn("Export failed: No data found for the given criteria.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"message\": \"No transactions found for the specified criteria.\"}");
                return;
            }

            // Set response headers for PDF download
            String filename = generateFilename("transactions", "pdf");
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            response.setContentLength(pdfBytes.length);

            // Write PDF bytes to response output stream
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
            log.info("Successfully generated and sent PDF file: {}", filename);

        } catch (IOException e) {
            log.error("IO Error writing PDF file to response: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Error during PDF export: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // Avoid writing detailed errors to response for security
            try {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"message\": \"An unexpected error occurred during PDF export.\"}");
            } catch (IOException ioException) {
                log.error("Error writing error response: {}", ioException.getMessage());
            }
        }
    }

    @PostMapping("/excel")
    public void exportToExcel(@RequestBody SearchCriteria criteria, HttpServletResponse response) {
        log.info("Received Excel export request with criteria: {}", criteria);
        try {
            // Validate essential criteria
            if (criteria.getAccountNumber() == null || criteria.getStartDate() == null || criteria.getEndDate() == null) {
                log.warn("Export failed: Missing required criteria (accountNumber, startDate, endDate).");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"message\": \"Account Number, Start Date, and End Date are required for export.\"}");
                return;
            }

            // Generate Excel byte array
            byte[] excelBytes = exportService.exportTransactionsToExcel(criteria);

            if (excelBytes == null || excelBytes.length == 0) {
                log.warn("Export failed: No data found for the given criteria.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"message\": \"No transactions found for the specified criteria.\"}");
                return;
            }

            // Set response headers for Excel download
            String filename = generateFilename("transactions", "xlsx");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            response.setContentLength(excelBytes.length);

            // Write Excel bytes to response output stream
            response.getOutputStream().write(excelBytes);
            response.getOutputStream().flush();
            log.info("Successfully generated and sent Excel file: {}", filename);

        } catch (IOException e) {
            log.error("IO Error writing Excel file to response: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Error during Excel export: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"message\": \"An unexpected error occurred during Excel export.\"}");
            } catch (IOException ioException) {
                log.error("Error writing error response: {}", ioException.getMessage());
            }
        }
    }

    private String generateFilename(String baseName, String extension) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        // Sanitize account number if present in criteria (optional, depends if needed in filename)
        // String accountNumber = criteria.getAccountNumber() != null ? criteria.getAccountNumber().replaceAll("[^a-zA-Z0-9]", "") : "all";
        return String.format("%s_export_%s.%s", baseName, timestamp, extension);
    }
}