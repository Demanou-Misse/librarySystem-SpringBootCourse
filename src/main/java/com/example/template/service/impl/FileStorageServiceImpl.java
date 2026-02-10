package com.example.template.service.impl;

import com.example.template.exception.AuthException; // Or a dedicated exception
import com.example.template.service.interfaces.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    // Constructor: initialize the main storage directory
    public FileStorageServiceImpl(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to create the storage directory.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        // 1. Security validation: clean the file name
        String originalFileName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename())
        );

        // 2. Updated Extension validation (Security 2026)
        // We now check based on the directory we are sending the file to
        if (subDirectory.equals("covers")) {
            if (!(originalFileName.toLowerCase().endsWith(".jpg") ||
                    originalFileName.toLowerCase().endsWith(".png") ||
                    originalFileName.toLowerCase().endsWith(".jpeg"))) {
                throw new RuntimeException("Only JPG and PNG file formats are allowed for covers.");
            }
        } else if (subDirectory.equals("pdfs")) {
            if (!originalFileName.toLowerCase().endsWith(".pdf")) {
                throw new RuntimeException("Only PDF file format is allowed for books.");
            }
        }

        try {
            // 3. Generate a unique file name to avoid collisions
            String fileName = UUID.randomUUID() + "_" + originalFileName;
            Path targetLocation = this.fileStorageLocation // Using your variable name
                    .resolve(subDirectory)
                    .resolve(fileName);

            // Create the subdirectory if it doesn't exist (e.g., uploads/covers)
            Files.createDirectories(targetLocation.getParent());

            // 4. Copy the file to the target location
            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Error occurred while storing the file.", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName, String subDirectory) {
        try {
            Path filePath = this.fileStorageLocation
                    .resolve(subDirectory)
                    .resolve(fileName)
                    .normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists())
                return resource;
            else
                throw new RuntimeException("File not found: " + fileName);
        } catch (Exception ex) {
            throw new RuntimeException("File not found: " + fileName, ex);
        }
    }

    @Override
    public void deleteFile(String fileName, String subDirectory) {
        // Useful when updating files to prevent server clutter
        try {
            Path filePath = this.fileStorageLocation.resolve(subDirectory).resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
