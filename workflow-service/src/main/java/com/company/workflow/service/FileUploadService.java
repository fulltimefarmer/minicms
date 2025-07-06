package com.company.workflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileUploadService {
    
    @Value("${file.upload.directory:./uploads}")
    private String uploadDirectory;
    
    public String uploadFile(MultipartFile file, String employeeId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFilename = String.format("%s_%s_%s.%s", 
                employeeId, timestamp, UUID.randomUUID().toString().substring(0, 8), fileExtension);
        
        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.write(filePath, file.getBytes());
        
        return filePath.toString();
    }
    
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Error deleting file: " + filePath + " - " + e.getMessage());
        }
    }
    
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    public byte[] readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }
        return Files.readAllBytes(path);
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
    
    public String getUploadDirectory() {
        return uploadDirectory;
    }
}