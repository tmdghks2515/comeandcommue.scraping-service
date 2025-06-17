package io.comeandcommue.scraping.service;

import io.comeandcommue.scraping.entity.PostEntity;
import io.comeandcommue.scraping.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public void saveNewPosts(List<PostEntity> posts) {
        if (posts == null || posts.isEmpty()) {
            return; // No posts to save
        }

        // Save all posts in a single batch operation
        postRepository.saveAll(posts);
    }
}
