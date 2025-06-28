package io.comeandcommue.scraping.application;

import io.comeandcommue.scraping.domain.post.PostEntity;
import io.comeandcommue.scraping.domain.post.PostRepository;
import io.comeandcommue.scraping.domain.scrap.ScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Transactional
@Component
public class ScrapHotPostsUseCase {
    private static final Logger log = Logger.getLogger(ScrapHotPostsUseCase.class.getName());

    private final PostRepository postRepository;
    private final ScrapService scrapService;
    private final RedisTemplate<String, String> redisTemplate;

    public int scrapHotPosts() {
        List<PostEntity> newHotPosts = new ArrayList<>();

        List<PostEntity> dcNewPosts = scrapService.scrapDcinsidePosts();
        newHotPosts.addAll(dcNewPosts);








        postRepository.saveAll(newHotPosts);

        return newHotPosts.size();
    }
}
