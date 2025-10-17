package com.example.welog.repository;

import com.example.welog.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    Optional<Tag> findByName(String name);
    
    List<Tag> findByNameIn(List<String> names);
    
    @Query("SELECT t FROM Tag t JOIN t.posts p GROUP BY t ORDER BY COUNT(p) DESC")
    List<Tag> findPopularTags();
    
    boolean existsByName(String name);
}
