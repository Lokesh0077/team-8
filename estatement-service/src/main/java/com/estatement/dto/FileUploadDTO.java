package com.estatement.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FileUploadDTO {
    private Long id;
    private String originalFilename;
    private LocalDateTime uploadTime;
    private String status;
    private int recordCount;
    private String errorMessage;
}