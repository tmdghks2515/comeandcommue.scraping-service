package io.comeandcommue.scraping.application;

import io.comeandcommue.scraping.common.CommunityType;
import io.comeandcommue.scraping.domain.post.PostEntity;
import io.comeandcommue.scraping.domain.post.PostRepository;
import io.comeandcommue.scraping.domain.scrap.ScrapService;
import io.comeandcommue.scraping.domain.scrap.StoredKeyStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Transactional
@Component
public class ScrapHotPostsUseCase {
    private static final Logger log = Logger.getLogger(ScrapHotPostsUseCase.class.getName());

    private final ScrapService scrapService;
    private final StoredKeyStore storedKeyStore;
    private final PostRepository postRepository;

    public int scrapHotPosts() {
        List<PostEntity> postsToSave = Arrays.stream(CommunityType.values())
                .flatMap(this::scrapUnstoredPostsByCommunity)
                .toList();

        List<PostEntity> savedPosts = postRepository.saveAll(postsToSave);
        savedPosts.forEach(it ->
                storedKeyStore.save(it.getCommunityType(), it.getKey())
        );

        return savedPosts.size();
    }

    private Stream<PostEntity> scrapUnstoredPostsByCommunity(CommunityType commuType) {
        return scrapService.scrapPostsByCommuType(commuType).stream()
                .filter(post -> !storedKeyStore.exists(commuType, post.getKey()));
    }
}
