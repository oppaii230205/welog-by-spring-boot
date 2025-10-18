package com.example.welog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.upload.dir}")
    private String uploadDir;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve images from /api/v1/img/** paths
        registry.addResourceHandler("/api/v1/img/users/**")
                .addResourceLocations("file:" + uploadDir + "/users/")
                .setCachePeriod(3600); // Cache for 1 hour
        
        registry.addResourceHandler("/api/v1/img/posts/**")
                .addResourceLocations("file:" + uploadDir + "/posts/")
                .setCachePeriod(3600); // Cache for 1 hour
        
        // Also serve from /img/** for direct access
        registry.addResourceHandler("/img/users/**")
                .addResourceLocations("file:" + uploadDir + "/users/")
                .setCachePeriod(3600);
        
        registry.addResourceHandler("/img/posts/**")
                .addResourceLocations("file:" + uploadDir + "/posts/")
                .setCachePeriod(3600);
    }

    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    //     registry.addMapping("/**");
    // }
}
