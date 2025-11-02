package com.estatement.repository;

import com.estatement.entity.FileUpload;
import com.estatement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

    /**
     * Finds all file uploads performed by a specific user,
     * ordered by the upload time in descending order (newest first).
     * @param user The user entity to search for.
     * @return A list of FileUpload entities.
     */
    List<FileUpload> findByUploadedByOrderByUploadTimeDesc(User user);
}