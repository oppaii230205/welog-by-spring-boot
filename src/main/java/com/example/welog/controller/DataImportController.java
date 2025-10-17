package com.example.welog.controller;

import com.example.welog.service.DataImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for data import operations
 * Only accessible by admins
 */
@RestController
@RequestMapping("/api/v1/admin/import")
@RequiredArgsConstructor
@Slf4j
public class DataImportController {

    private final DataImportService dataImportService;

    /**
     * Import scraped posts from JSON file
     * POST /api/v1/admin/import/posts?filePath=/path/to/file.json
     */
    @PostMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> importPosts(
            @RequestParam(defaultValue = "scrapy_project/blogcrawler/data/devto_posts.json") String filePath) {
        
        log.info("Admin triggered import from: {}", filePath);
        
        try {
            DataImportService.ImportResult result = dataImportService.importScrapedPosts(filePath);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Import completed successfully",
                    "statistics", Map.of(
                            "imported", result.getSuccessCount(),
                            "skipped", result.getSkippedCount(),
                            "errors", result.getErrorCount()
                    )
            ));
            
        } catch (Exception e) {
            log.error("Import failed: {}", e.getMessage());
            
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Import failed: " + e.getMessage()
            ));
        }
    }

    /**
     * Get import status/statistics
     * GET /api/v1/admin/import/status
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getImportStatus() {
        // This could be expanded to show import history, last import time, etc.
        return ResponseEntity.ok(Map.of(
                "message", "Import endpoint is ready",
                "availableEndpoints", Map.of(
                        "importPosts", "POST /api/v1/admin/import/posts?filePath=path/to/file.json"
                )
        ));
    }
}