package com.example.welog.utils;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.welog.dto.CommentResponseDto;
import com.example.welog.dto.PostResponseDto;
import com.example.welog.dto.TagResponseDto;
import com.example.welog.dto.UserResponseDto;
import com.example.welog.model.Comment;
import com.example.welog.model.Post;
import com.example.welog.model.User;

public class ResponseDtoMapper {
    public static UserResponseDto mapToUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoto()
        );
    }

    public static PostResponseDto mapToPostResponseDto(Post post) {
        Set<TagResponseDto> tagDtos = post.getTags().stream()
                .map(tag -> new TagResponseDto(tag.getId(), tag.getName()))
                .collect(Collectors.toSet());

        UserResponseDto authorDto = mapToUserResponseDto(post.getAuthor());

        Set<CommentResponseDto> commentsDto = post.getComments().stream()
                .map(ResponseDtoMapper::mapToCommentResponseDto)
                .collect(Collectors.toSet());

        return new PostResponseDto(
                post.getId(),
                post.getSlug(),
                post.getTitle(),
                post.getContent(),
                post.getExcerpt(),
                post.getCoverImage(),
                authorDto,
                post.getCreatedAt(),
                tagDtos,
                commentsDto
        );
    }

    public static CommentResponseDto mapToCommentResponseDto(Comment comment) {
        UserResponseDto userDto = mapToUserResponseDto(comment.getUser());
        // PostResponseDto postDto = mapToPostResponseDto(comment.getPost());

        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                // postDto,
                userDto,
                comment.getCreatedAt()
        );
    }
}
