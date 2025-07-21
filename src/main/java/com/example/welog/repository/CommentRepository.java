package com.example.welog.repository;

import com.example.welog.model.Comment;

import java.util.List;

// import com.example.welog.model.Post;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
        List<Comment> findByPostId(Long postId);
    
//     Page<Comment> findByPost(Post post, Pageable pageable);
    
//     List<Comment> findByPostOrderByCreatedAtDesc(Post post);
    
//     long countByPost(Post post);
}
