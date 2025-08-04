package com.example.welog.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.welog.dto.CommentResponseDto;
import com.example.welog.dto.PostResponseDto;
import com.example.welog.dto.TagResponseDto;
import com.example.welog.dto.UserResponseDto;
import com.example.welog.model.Comment;
import com.example.welog.model.Post;
import com.example.welog.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseDtoMapper {
    private static final Logger logger = LoggerFactory.getLogger(ResponseDtoMapper.class);

    public static UserResponseDto mapToUserResponseDto(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoto(),
                roles
        );
    }

    public static PostResponseDto mapToPostResponseDto(Post post) {
//        logger.debug("========= mapToPostResponseDto =========");

//        logger.debug("Tags of post: {}", post.getTags().size());

//        logger.debug("Comments of post: {}", post.getComments().size());

        Set<TagResponseDto> tagDtos = post.getTags() != null
                ? new HashSet<>(post.getTags()).stream()
                .map(tag -> new TagResponseDto(tag.getId(), tag.getName()))
                .collect(Collectors.toSet())
                : new HashSet<>();

        UserResponseDto authorDto = mapToUserResponseDto(post.getAuthor());

        Set<CommentResponseDto> commentsDto = post.getComments() != null ? post.getComments().stream()
//                .filter(comment -> comment.getDeletedAt() == null)
                .map(ResponseDtoMapper::mapToCommentResponseDto)
                .collect(Collectors.toSet()) : new HashSet<>();

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
