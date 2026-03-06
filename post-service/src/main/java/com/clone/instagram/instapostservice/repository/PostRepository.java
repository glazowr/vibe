package com.vibe.instapostservice.repository;

import com.vibe.instapostservice.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByUsernameOrderByCreatedAtDesc(String username);

    // to get the post of set of ids you follow
    List<Post> findByIdInOrderByCreatedAtDesc(List<String> ids);
}
