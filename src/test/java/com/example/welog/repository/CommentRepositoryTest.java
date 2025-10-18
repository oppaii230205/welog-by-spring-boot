// package com.example.welog.repository;

// import com.example.welog.model.Comment;
// import com.example.welog.model.Post;
// import com.example.welog.model.User;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;

// import java.time.OffsetDateTime;
// import java.util.List;

// import static org.assertj.core.api.Assertions.assertThat;

// @DataJpaTest
// class CommentRepositoryTest {

//     @Autowired
//     private TestEntityManager entityManager;

//     @Autowired
//     private CommentRepository commentRepository;

//     private Comment comment;

//     @BeforeEach
//     void setUp() {
//         entityManager.clear();

//         User author = new User();
//         author.setName("Comment Author");
//         author = entityManager.persist(author);

//         Post post = new Post();
//         post.setTitle("Test Post");
//         post.setAuthor(author);
//         post = entityManager.persist(post);

//         comment = new Comment();
//         comment.setContent("Test comment");
//         comment.setPost(post);
//         comment.setUser(author);
//         comment.setParent(null);
//         comment.setLevel(1);
//         comment.setCreatedAt(OffsetDateTime.now());
//         comment = entityManager.persist(comment);
//         entityManager.flush();
//     }

//     @Test
//     void findByPostId_Exists_ReturnsCommentsForPost() {
//         List<Comment> comments = commentRepository.findByPostId(comment.getPost().getId());

//         assertThat(comments).hasSize(1);
//         assertThat(comments.getFirst().getContent()).isEqualTo("Test comment");
//     }

//     @Test
//     void softDelete_RemovesCommentFromFindResults() {
//         commentRepository.softDelete(comment.getId());
//         entityManager.flush();
//         entityManager.clear();

//         List<Comment> comments = commentRepository.findByPostId(comment.getPost().getId());
//         assertThat(comments).isEmpty();
//     }
// }