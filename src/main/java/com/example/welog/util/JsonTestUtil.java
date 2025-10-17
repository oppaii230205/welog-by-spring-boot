package com.example.welog.util;

import com.example.welog.dto.ScrapedPostDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Simple utility to test JSON reading
 */
public class JsonTestUtil {
    
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonFilePath = "scrapy_project/blogcrawler/data/devto_posts.json";
        
        System.out.println("Testing JSON file reading...");
        System.out.println("Looking for file at: " + new File(jsonFilePath).getAbsolutePath());
        
        try {
            File file = new File(jsonFilePath);
            if (!file.exists()) {
                System.err.println("❌ File not found: " + file.getAbsolutePath());
                System.out.println("💡 Make sure the scraped JSON file exists at this location");
                return;
            }
            
            List<ScrapedPostDto> posts = objectMapper.readValue(file, new TypeReference<List<ScrapedPostDto>>() {});
            
            System.out.println("✅ Successfully read JSON file!");
            System.out.println("📊 Found " + posts.size() + " posts");
            
            if (!posts.isEmpty()) {
                ScrapedPostDto first = posts.get(0);
                System.out.println("\n📝 Sample post:");
                System.out.println("   Title: " + first.getTitle());
                System.out.println("   Author: " + first.getAuthorName());
                System.out.println("   Tags: " + first.getTags());
                System.out.println("   URL: " + first.getUrl());
            }
            
            System.out.println("\n🎉 JSON structure is compatible with the import system!");
            
        } catch (IOException e) {
            System.err.println("❌ Failed to read JSON: " + e.getMessage());
            System.out.println("💡 Check the JSON file format and structure");
        }
    }
}