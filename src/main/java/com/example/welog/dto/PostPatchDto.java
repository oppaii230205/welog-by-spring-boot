package com.example.welog.dto;

public class PostPatchDto {
    private String title;
    private String content;
    private String coverImage;
    private Long author;

    public PostPatchDto() {}

    public PostPatchDto(String title, String content, String coverImage, Long author) {
        this.title = title;
        this.content = content;
        this.coverImage = coverImage;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Long getAuthor() {
        return author;
    }

    public void setAuthor(Long author) {
        this.author = author;
    }
}
