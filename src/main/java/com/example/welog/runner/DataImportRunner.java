package com.example.welog.runner;

import com.example.welog.service.DataImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Command line runner to import scraped dev.to posts
 * Only runs when 'import' profile is active
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("import")
public class DataImportRunner implements CommandLineRunner {

    private final DataImportService dataImportService;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Starting Data Import Runner ===");
        
        // Default JSON file path - can be overridden via command line args
        String jsonFilePath = "scrapy_project/blogcrawler/data/devto_posts.json";
        
        // Filter out Spring Boot arguments and look for actual file path
        for (String arg : args) {
            if (!arg.startsWith("--spring.") && !arg.startsWith("--logging.") && !arg.trim().isEmpty()) {
                jsonFilePath = arg;
                break;
            }
        }
        
        log.info("Importing data from: {}", jsonFilePath);
        
        try {
            DataImportService.ImportResult result = dataImportService.importScrapedPosts(jsonFilePath);
            
            log.info("=== Import Completed ===");
            log.info("✅ Successfully imported: {} posts", result.getSuccessCount());
            log.info("⏭️ Skipped (duplicates): {} posts", result.getSkippedCount());
            log.info("❌ Errors: {} posts", result.getErrorCount());
            log.info("📊 Total processed: {} posts", 
                    result.getSuccessCount() + result.getSkippedCount() + result.getErrorCount());
            
            if (result.getSuccessCount() > 0) {
                log.info("🎉 Great! You now have {} new sample posts with realistic data!", result.getSuccessCount());
                log.info("💡 Tip: Check your database for new posts, authors, and tags!");
            }
            
        } catch (Exception e) {
            log.error("❌ Import failed: {}", e.getMessage());
            log.error("💡 Make sure the JSON file exists and has correct format");
            throw e;
        }
    }
}