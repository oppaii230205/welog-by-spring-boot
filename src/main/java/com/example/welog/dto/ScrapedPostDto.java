package com.example.welog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for mapping scraped dev.to post data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrapedPostDto {
    private String url;
    private String slug;
    private String title;
    private String content;
    private String excerpt;

    @JsonProperty("author_name")
    private String authorName;
    
    @JsonProperty("author_username")
    private String authorUsername;

    @JsonProperty("author_profile_url")
    private String authorProfileUrl;

    @JsonProperty("author_avatar")
    private String authorAvatar;

    @JsonProperty("published_at")
    private String publishedAt;  
    
    @JsonProperty("updated_at")
    private String updatedAt;    // Will be parsed to OffsetDateTime
    
    @JsonProperty("reading_time")
    private Integer readingTime;

    @JsonProperty("likes_count")
    private Integer likesCount;

    @JsonProperty("comments_count")
    private Integer commentsCount;

    @JsonProperty("bookmarks_count")
    private Integer bookmarksCount;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("cover_image")
    private String coverImage;
    
    @JsonProperty("scraped_at")
    private String scrapedAt;  

    @JsonProperty("source_website")
    private String sourceWebsite;

    @JsonProperty("post_id")
    private String postId;
}