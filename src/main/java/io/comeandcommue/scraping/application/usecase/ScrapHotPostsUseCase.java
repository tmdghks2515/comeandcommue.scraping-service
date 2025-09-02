package io.comeandcommue.scraping.application.usecase;

import io.comeandcommue.scraping.common.enums.ScrapTargetType;
import io.comeandcommue.scraping.domain.post.PostEntity;
import io.comeandcommue.scraping.domain.post.PostRepository;
import io.comeandcommue.scraping.domain.scrap.ScrapInfoEntity;
import io.comeandcommue.scraping.domain.scrap.ScrapInfoRepository;
import io.comeandcommue.scraping.domain.scrap.ScrapService;
import io.comeandcommue.scraping.domain.scrap.StoredKeyStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Transactional
@Component
public class ScrapHotPostsUseCase {
    private static final Logger log = Logger.getLogger(ScrapHotPostsUseCase.class.getName());

    private final ScrapService scrapService;
    private final StoredKeyStore storedKeyStore;
    private final PostRepository postRepository;
    private final ScrapInfoRepository scrapInfoRepository;

    public int scrapRealtimeHotPosts() {
        List<ScrapInfoEntity> scrapInfoList = scrapInfoRepository.findAllByScrapTargetType(ScrapTargetType.REALTIME_HOT_POSTS);

        List<PostEntity> postsToSave = scrapService.scrapRealtimeHotPosts(scrapInfoList)
                .stream().filter(post -> !storedKeyStore.exists(post.getCommunityType(), post.getKey()))
                .toList();

        List<PostEntity> savedPosts = postRepository.saveAll(postsToSave);
        savedPosts.forEach(it ->
                storedKeyStore.save(it.getCommunityType(), it.getKey())
        );

        return savedPosts.size();
    }
}
