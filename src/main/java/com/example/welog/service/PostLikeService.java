package com.example.welog.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.welog.dto.UserResponseDto;
import com.example.welog.model.Notification;
import com.example.welog.model.Post;
import com.example.welog.model.PostLike;
import com.example.welog.model.PostLikeId;
import com.example.welog.model.User;
import com.example.welog.repository.NotificationRepository;
import com.example.welog.repository.PostLikeRepository;
import com.example.welog.repository.PostRepository;
import com.example.welog.repository.UserRepository;
import com.example.welog.utils.ResponseDtoMapper;

import jakarta.transaction.Transactional;

@Service
public class PostLikeService {
    private static final Logger logger = LoggerFactory.getLogger(PostLikeService.class);
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public PostLikeService(PostLikeRepository postLikeRepository, PostRepository postRepository, UserRepository userRepository, NotificationRepository notificationRepository) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void likePost(Long userId, Long postId) {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        // check if already liked
        boolean exists = postLikeRepository.existsByUserIdAndPostId(user.getId(), post.getId());
        if (exists) throw new RuntimeException("Already liked");

        PostLike like = new PostLike();
        like.setUser(user);
        like.setPost(post);
        like.setId(new PostLikeId(user.getId(), post.getId()));
        postLikeRepository.save(like);

        // create notification
        if (!post.getAuthor().getId().equals(userId)) { // don't notify self-like
            Notification notification = new Notification();
            notification.setRecipient(post.getAuthor());
            notification.setSender(user);
            notification.setPost(post);
            notification.setType("LIKE");
            notification.setMessage(user.getName() + " liked your post: " + post.getTitle());
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void unlikePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        PostLikeId likeId = new PostLikeId(user.getId(), post.getId());
        PostLike like = postLikeRepository.findById(likeId)
            .orElseThrow(() -> new RuntimeException("Like not found"));

        postLikeRepository.delete(like);

        // Optionally, delete the notification related to this like
        // List<Notification> notifications = notificationRepository.findByRecipientId(post.getAuthor().getId());
        // for (Notification notification : notifications) {
        //     if (notification.getSender().getId().equals(userId) &&
        //         notification.getPost() != null &&
        //         notification.getPost().getId().equals(postId) &&
        //         "LIKE".equals(notification.getType())) {
        //         notificationRepository.delete(notification);
        //         break; // assuming only one such notification exists
        //     }
        // }
    }

    public List<UserResponseDto> getLikes(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        return postLikeRepository.findByPostId(post.getId()).stream()
            .map(PostLike::getUser)
            .map(ResponseDtoMapper::mapToUserResponseDto)
            .toList();
    }

}
