package com.example.welog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    private static final Logger logger = LoggerFactory.getLogger(SupabaseStorageService.class);

    private final WebClient webClient;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    public SupabaseStorageService(WebClient supabaseWebClient) {
        this.webClient = supabaseWebClient;
    }

    /**
     * Upload file to Supabase Storage
     * @param file MultipartFile to upload
     * @param bucketName Bucket name (user-avatars or post-covers)
     * @return Public URL of uploaded file
     */
    public String uploadFile(MultipartFile file, String bucketName) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // Determine content type
        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }

        // Upload to Supabase Storage
        try {
            String uploadPath = String.format("/storage/v1/object/%s/%s", bucketName, filename);
            
            logger.info("Uploading file to Supabase: bucket={}, filename={}, size={} bytes", 
                    bucketName, filename, file.getSize());

            String response = webClient.post()
                    .uri(uploadPath)
                    .contentType(MediaType.parseMediaType(contentType))
                    .bodyValue(file.getBytes())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Return public URL
            String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", 
                    supabaseUrl, bucketName, filename);
            
            logger.info("File uploaded successfully: {}", publicUrl);
            return publicUrl;

        } catch (WebClientResponseException e) {
            logger.error("Failed to upload file to Supabase. Status: {}, Response: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new IOException("Failed to upload file to Supabase: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error uploading file to Supabase: {}", e.getMessage());
            throw new IOException("Failed to upload file to Supabase", e);
        }
    }

    /**
     * Delete file from Supabase Storage
     * @param fileUrl Full URL of the file to delete
     * @param bucketName Bucket name
     */
    public void deleteFile(String fileUrl, String bucketName) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            logger.warn("Cannot delete file: URL is null or empty");
            return;
        }

        // Only delete if it's a Supabase URL
        if (!fileUrl.contains(supabaseUrl)) {
            logger.info("Skipping delete: URL is not from Supabase storage");
            return;
        }

        try {
            // Extract filename from URL
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            
            String deletePath = String.format("/storage/v1/object/%s/%s", bucketName, filename);
            
            logger.info("Deleting file from Supabase: bucket={}, filename={}", bucketName, filename);

            webClient.delete()
                    .uri(deletePath)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            logger.info("File deleted successfully: {}", filename);

        } catch (WebClientResponseException e) {
            logger.error("Failed to delete file from Supabase. Status: {}, Response: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Unexpected error deleting file from Supabase: {}", e.getMessage());
        }
    }

    /**
     * Get public URL for a file
     * @param bucketName Bucket name
     * @param filename Filename
     * @return Public URL
     */
    public String getPublicUrl(String bucketName, String filename) {
        return String.format("%s/storage/v1/object/public/%s/%s", 
                supabaseUrl, bucketName, filename);
    }
}
