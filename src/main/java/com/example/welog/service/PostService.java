package com.example.welog.service;

import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.welog.dto.PostCreateDto;
import com.example.welog.dto.PostPatchDto;
import com.example.welog.dto.PostResponseDto;
import com.example.welog.exception.ResourceNotFoundException;
import com.example.welog.model.Post;
import com.example.welog.model.User;
import com.example.welog.repository.PostRepository;
import com.example.welog.repository.UserRepository;
import com.example.welog.utils.ResponseDtoMapper;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<PostResponseDto> getAllPosts(Pageable pageable) {
        Page<Post> page = postRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))));

        return page.getContent()
                .stream()
                .map(ResponseDtoMapper::mapToPostResponseDto)
                .toList();
    }

    public PostResponseDto getPost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }

        return ResponseDtoMapper.mapToPostResponseDto(postRepository.findById(id).get());
    }

    @Transactional
    public PostResponseDto createPost(PostCreateDto postCreateDto) {
        User author = userRepository.findById(postCreateDto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + postCreateDto.getAuthorId()));

        Post post = new Post(
                postCreateDto.getTitle(),
                postCreateDto.getContent(),
                postCreateDto.getCoverImage(),
                author
        );

        Post savedPost = postRepository.save(post);
        return ResponseDtoMapper.mapToPostResponseDto(savedPost);
    }

    public PostResponseDto updatePost(Long id, PostPatchDto postPatchDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        if (postPatchDto.getTitle() != null) {
            post.setTitle(postPatchDto.getTitle());
        }
        if (postPatchDto.getContent() != null) {
            post.setContent(postPatchDto.getContent());
        }
        if (postPatchDto.getCoverImage() != null) {
            post.setCoverImage(postPatchDto.getCoverImage());
        }
        if (postPatchDto.getAuthorId() != null) {
            User author = userRepository.findById(postPatchDto.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + postPatchDto.getAuthorId()));

            post.setAuthor(author);
        }

        Post updatedPost = postRepository.save(post);
        return ResponseDtoMapper.mapToPostResponseDto(updatedPost);
    }

    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }

    public PostResponseDto getPostBySlug(String slug) {
        return postRepository.findBySlug(slug)
                .map(ResponseDtoMapper::mapToPostResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with slug: " + slug));
    }
}

// import com.example.welog.dto.PostCreateDto;
// import com.example.welog.dto.PostResponseDto;
// import com.example.welog.dto.UserResponseDto;
// import com.example.welog.dto.TagResponseDto;
// import com.example.welog.exception.ResourceNotFoundException;
// import com.example.welog.model.Post;
// import com.example.welog.model.Tag;
// import com.example.welog.model.User;
// import com.example.welog.repository.PostRepository;
// import com.example.welog.repository.TagRepository;
// import com.example.welog.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// @Transactional
// public class PostService {
    
//     private final PostRepository postRepository;
//     private final UserRepository userRepository;
//     private final TagRepository tagRepository;
    
//     @Autowired
//     public PostService(PostRepository postRepository, UserRepository userRepository, TagRepository tagRepository) {
//         this.postRepository = postRepository;
//         this.userRepository = userRepository;
//         this.tagRepository = tagRepository;
//     }
    
//     public PostResponseDto createPost(PostCreateDto postCreateDto, Long authorId) {
//         User author = userRepository.findById(authorId)
//             .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));
        
//         Post post = new Post();
//         post.setTitle(postCreateDto.getTitle());
//         post.setContent(postCreateDto.getContent());
//         post.setPublished(postCreateDto.isPublished());
//         post.setAuthor(author);
        
//         // Handle tags
//         if (postCreateDto.getTagNames() != null && !postCreateDto.getTagNames().isEmpty()) {
//             List<Tag> tags = handleTags(postCreateDto.getTagNames());
//             post.setTags(tags);
//         }
        
//         Post savedPost = postRepository.save(post);
//         return convertToResponseDto(savedPost);
//     }
    
//     @Transactional(readOnly = true)
//     public PostResponseDto getPostById(Long id) {
//         Post post = postRepository.findById(id)
//             .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
//         return convertToResponseDto(post);
//     }
    
//     @Transactional(readOnly = true)
//     public PostResponseDto getPostBySlug(String slug) {
//         Post post = postRepository.findBySlug(slug)
//             .orElseThrow(() -> new ResourceNotFoundException("Post not found with slug: " + slug));
//         return convertToResponseDto(post);
//     }
    
//     @Transactional(readOnly = true)
//     public Page<PostResponseDto> getAllPosts(Pageable pageable) {
//         return postRepository.findAll(pageable).map(this::convertToResponseDto);
//     }
    
//     @Transactional(readOnly = true)
//     public Page<PostResponseDto> getPublishedPosts(Pageable pageable) {
//         return postRepository.findByPublishedTrue(pageable).map(this::convertToResponseDto);
//     }
    
//     @Transactional(readOnly = true)
//     public Page<PostResponseDto> getPostsByAuthor(Long authorId, Pageable pageable) {
//         User author = userRepository.findById(authorId)
//             .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));
//         return postRepository.findByAuthor(author, pageable).map(this::convertToResponseDto);
//     }
    
//     @Transactional(readOnly = true)
//     public Page<PostResponseDto> searchPosts(String keyword, Pageable pageable) {
//         return postRepository.findPublishedPostsByKeyword(keyword, pageable).map(this::convertToResponseDto);
//     }
    
//     public PostResponseDto updatePost(Long id, PostCreateDto postUpdateDto) {
//         Post post = postRepository.findById(id)
//             .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        
//         post.setTitle(postUpdateDto.getTitle());
//         post.setContent(postUpdateDto.getContent());
//         post.setPublished(postUpdateDto.isPublished());
        
//         // Handle tags
//         if (postUpdateDto.getTagNames() != null) {
//             List<Tag> tags = handleTags(postUpdateDto.getTagNames());
//             post.setTags(tags);
//         }
        
//         Post updatedPost = postRepository.save(post);
//         return convertToResponseDto(updatedPost);
//     }
    
//     public void deletePost(Long id) {
//         Post post = postRepository.findById(id)
//             .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
//         postRepository.delete(post);
//     }
    
//     private List<Tag> handleTags(List<String> tagNames) {
//         List<Tag> tags = new ArrayList<>();
//         for (String tagName : tagNames) {
//             Tag tag = tagRepository.findByName(tagName)
//                 .orElseGet(() -> {
//                     Tag newTag = new Tag(tagName);
//                     return tagRepository.save(newTag);
//                 });
//             tags.add(tag);
//         }
//         return tags;
//     }
    
//     private PostResponseDto convertToResponseDto(Post post) {
//         PostResponseDto dto = new PostResponseDto();
//         dto.setId(post.getId());
//         dto.setTitle(post.getTitle());
//         dto.setContent(post.getContent());
//         dto.setSlug(post.getSlug());
//         dto.setPublished(post.isPublished());
//         dto.setCreatedAt(post.getCreatedAt());
//         dto.setUpdatedAt(post.getUpdatedAt());
        
//         // Convert author
//         if (post.getAuthor() != null) {
//             User author = post.getAuthor();
//             UserResponseDto authorDto = new UserResponseDto(
//                 author.getId(),
//                 author.getUsername(),
//                 author.getEmail(),
//                 author.getFirstName(),
//                 author.getLastName(),
//                 author.getCreatedAt(),
//                 author.getUpdatedAt()
//             );
//             dto.setAuthor(authorDto);
//         }
        
//         // Convert tags
//         if (post.getTags() != null) {
//             List<TagResponseDto> tagDtos = post.getTags().stream()
//                 .map(tag -> new TagResponseDto(tag.getId(), tag.getName(), tag.getSlug(), tag.getDescription()))
//                 .collect(Collectors.toList());
//             dto.setTags(tagDtos);
//         }
        
//         // Set comment count (if needed)
//         dto.setCommentCount(post.getComments() != null ? post.getComments().size() : 0);
        
//         return dto;
//     }
// }
