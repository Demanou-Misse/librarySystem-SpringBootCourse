package com.example.template.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subDirectory);
    Resource loadFileAsResource(String fileName, String subDirectory);
    void deleteFile(String fileName, String subDirectory);
}

