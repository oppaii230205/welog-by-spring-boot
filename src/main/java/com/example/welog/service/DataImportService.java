package com.example.welog.service;

import com.example.welog.dto.ScrapedPostDto;
import com.example.welog.model.*;
import com.example.welog.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataImportService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    /**
     * Import scraped posts from JSON file
     * @param jsonFilePath Path to the scraped posts JSON file
     * @return Number of posts imported
     */
    @Transactional
    public ImportResult importScrapedPosts(String jsonFilePath) {
        log.info("Starting import from: {}", jsonFilePath);
        
        ImportResult result = new ImportResult();
        
        try {
            // Read JSON file
            List<ScrapedPostDto> scrapedPosts = readScrapedPostsFromFile(jsonFilePath);
            log.info("Found {} posts to import", scrapedPosts.size());
            log.info("First scraped post: {}", scrapedPosts.isEmpty() ? "N/A" : scrapedPosts.get(0));
            
            // Ensure default roles exist
            ensureDefaultRolesExist();
            
            // Process each post
            for (ScrapedPostDto scrapedPost : scrapedPosts) {
                try {
                    importSinglePost(scrapedPost, result);
                } catch (Exception e) {
                    log.error("Failed to import post: {} - Error: {}", scrapedPost.getTitle(), e.getMessage());
                    result.incrementErrors();
                }
            }
            
            log.info("Import completed. Success: {}, Skipped: {}, Errors: {}", 
                    result.getSuccessCount(), result.getSkippedCount(), result.getErrorCount());
            
        } catch (Exception e) {
            log.error("Failed to read JSON file: {}", e.getMessage());
            throw new RuntimeException("Import failed", e);
        }
        
        return result;
    }
    
    private List<ScrapedPostDto> readScrapedPostsFromFile(String jsonFilePath) throws IOException {
        File file = new File(jsonFilePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + jsonFilePath);
        }
        
        return objectMapper.readValue(file, new TypeReference<List<ScrapedPostDto>>() {});
    }
    
    private void importSinglePost(ScrapedPostDto scrapedPost, ImportResult result) {
        // Skip if post already exists (by slug or external post_id)
        String cleanSlug = generateCleanSlug(scrapedPost.getTitle());
        if (postRepository.findBySlug(cleanSlug).isPresent()) {
            log.debug("Post already exists, skipping: {}", scrapedPost.getTitle());
            result.incrementSkipped();
            return;
        }
        
        // Create or get author
        User author = createOrGetAuthor(scrapedPost);

        log.info("Importing post: {} by author: {}", scrapedPost.getTitle(), author.getName());
        
        // Create post
        Post post = new Post();
        post.setTitle(scrapedPost.getTitle());
        post.setSlug(cleanSlug);
        post.setContent(cleanScrapedContent(scrapedPost.getContent()));
        post.setExcerpt(generateExcerpt(scrapedPost));
        post.setCoverImage(scrapedPost.getCoverImage());
        post.setAuthor(author);
        
        // Handle tags
        if (scrapedPost.getTags() != null && !scrapedPost.getTags().isEmpty()) {
            Set<Tag> tags = createOrGetTags(scrapedPost.getTags());
            post.setTags(tags);
        }
        
        // Save post
        postRepository.save(post);
        result.incrementSuccess();
        
        log.debug("Imported post: {}", scrapedPost.getTitle());
    }
    
    private User createOrGetAuthor(ScrapedPostDto scrapedPost) {
        String email = generateAuthorEmail(scrapedPost);
        
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User author = new User();
                    author.setName(scrapedPost.getAuthorUsername());
                    author.setEmail(email);
                    author.setPhoto(scrapedPost.getAuthorAvatar()); // Default avatar
                    author.setPassword(passwordEncoder.encode("test1234")); // Default password
                    
                    // Assign default role
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Default user role not found"));
                    author.setRoles(Set.of(userRole));
                    
                    return userRepository.save(author);
                });
    }
    
    private Set<Tag> createOrGetTags(List<String> tagNames) {
        return tagNames.stream()
                .filter(Objects::nonNull)
                .filter(name -> !name.trim().isEmpty())
                .map(this::createOrGetTag)
                .collect(Collectors.toSet());
    }
    
    private Tag createOrGetTag(String tagName) {
        String cleanName = cleanTagName(tagName);
        
        return tagRepository.findByName(cleanName)
                .orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setName(cleanName);
                    return tagRepository.save(tag);
                });
    }
    
    private void ensureDefaultRolesExist() {
        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(ERole.ROLE_USER);
            roleRepository.save(userRole);
            log.info("Created default ROLE_USER");
        }
        
        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
            log.info("Created default ROLE_ADMIN");
        }
    }
    
    // Utility methods for data cleaning
    
    private String generateCleanSlug(String title) {
        if (title == null) return "untitled-post-" + System.currentTimeMillis();
        
        String baseSlug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .trim();
                
        // Ensure uniqueness by appending timestamp if needed
        String uniqueSlug = baseSlug;
        int counter = 1;
        while (postRepository.findBySlug(uniqueSlug).isPresent()) {
            uniqueSlug = baseSlug + "-" + counter++;
        }
        
        return uniqueSlug;
    }
    
    private String cleanScrapedContent(String content) {
        if (content == null) return "";
        
        // Remove or replace problematic HTML that might not display well
        return content
                .replaceAll("<svg[^>]*>.*?</svg>", "") // Remove SVG elements
                .replaceAll("class=\"[^\"]*\"", "") // Remove CSS classes
                .replaceAll("style=\"[^\"]*\"", "") // Remove inline styles
                .trim();
    }
    
    private String generateExcerpt(ScrapedPostDto scrapedPost) {
        if (scrapedPost.getExcerpt() != null && !scrapedPost.getExcerpt().trim().isEmpty()) {
            return scrapedPost.getExcerpt().substring(0, Math.min(scrapedPost.getExcerpt().length(), 200));
        }
        
        if (scrapedPost.getContent() != null) {
            // Extract plain text from HTML
            String plainText = scrapedPost.getContent()
                    .replaceAll("<[^>]*>", " ") // Remove HTML tags
                    .replaceAll("\\s+", " ") // Normalize whitespace
                    .trim();
            
            return plainText.substring(0, Math.min(plainText.length(), 200));
        }
        
        return "No excerpt available";
    }
    
    private String generateAuthorEmail(ScrapedPostDto scrapedPost) {
        String username = scrapedPost.getAuthorUsername();
        if (username == null || username.trim().isEmpty()) {
            username = scrapedPost.getAuthorName();
        }
        
        if (username == null || username.trim().isEmpty()) {
            username = "unknown-author";
        }
        
        // Clean username and create email
        String cleanUsername = username.toLowerCase()
                .replaceAll("[^a-z0-9]", "")
                .trim();
                
        if (cleanUsername.isEmpty()) {
            cleanUsername = "author" + System.currentTimeMillis();
        }
        
        return cleanUsername + "@devto.imported.local";
    }
    
    private String cleanAuthorName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Imported Author";
        }
        
        return name.trim().substring(0, Math.min(name.trim().length(), 100));
    }
    
    private String cleanTagName(String tagName) {
        if (tagName == null) return "";
        
        return tagName.trim()
                .toLowerCase()
                .substring(0, Math.min(tagName.trim().length(), 50));
    }
    
    /**
     * Result class to track import statistics
     */
    public static class ImportResult {
        private int successCount = 0;
        private int skippedCount = 0;
        private int errorCount = 0;
        
        public void incrementSuccess() { successCount++; }
        public void incrementSkipped() { skippedCount++; }
        public void incrementErrors() { errorCount++; }
        
        public int getSuccessCount() { return successCount; }
        public int getSkippedCount() { return skippedCount; }
        public int getErrorCount() { return errorCount; }
        
        @Override
        public String toString() {
            return String.format("ImportResult{success=%d, skipped=%d, errors=%d}", 
                    successCount, skippedCount, errorCount);
        }
    }
}