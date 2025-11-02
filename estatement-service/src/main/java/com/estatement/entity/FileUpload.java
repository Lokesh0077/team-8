package com.estatement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "file_uploads")
public class FileUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @Column(name = "record_count")
    private int recordCount;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_user_id") // Maps to the user who uploaded it
    private User uploadedBy;

    private String status; // e.g., "PROCESSING", "COMPLETED", "FAILED"

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}