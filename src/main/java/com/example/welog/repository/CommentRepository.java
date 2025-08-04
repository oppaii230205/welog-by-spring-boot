package com.example.welog.repository;

import com.example.welog.model.Comment;

import java.util.List;

// import com.example.welog.model.Post;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;
// import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
        List<Comment> findByPostId(Long postId);

        @Modifying
        @Transactional
        @NativeQuery("UPDATE comments SET deleted_at = NOW() where id=?1")
        void softDelete(Long id);
}
