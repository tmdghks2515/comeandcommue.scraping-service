package io.comeandcommue.scraping.application.usecase;

import io.comeandcommue.scraping.common.enums.ScrapTargetType;
import io.comeandcommue.scraping.domain.post.PostEntity;
import io.comeandcommue.scraping.domain.post.PostRepository;
import io.comeandcommue.scraping.domain.scrap.ScrapInfoEntity;
import io.comeandcommue.scraping.domain.scrap.ScrapInfoRepository;
import io.comeandcommue.scraping.domain.scrap.ScrapService;
import io.comeandcommue.scraping.domain.scrap.StoredPostStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Transactional
@Component
public class ScrapHotPostsUseCase {
    private static final Logger log = Logger.getLogger(ScrapHotPostsUseCase.class.getName());

    private final ScrapService scrapService;
    private final StoredPostStore storedKeyStore;
    private final PostRepository postRepository;
    private final ScrapInfoRepository scrapInfoRepository;

    public int scrapRealtimeHotPosts() {
        List<ScrapInfoEntity> scrapInfoList = scrapInfoRepository.findAllByScrapTargetType(ScrapTargetType.REALTIME_HOT_POSTS);

        if (scrapInfoList == null || scrapInfoList.isEmpty()) {
            return 0;
        }

        List<PostEntity> scrappedPosts = scrapService.scrapRealtimeHotPosts(scrapInfoList);
        if (scrappedPosts == null || scrappedPosts.isEmpty()) {
            return 0;
        }

        List<PostEntity> postsToSave = new ArrayList<>();

        scrappedPosts.forEach(it -> {
                    if(storedKeyStore.saveStoredPostKey(it.getCommunityType(), it.getKey()))
                        postsToSave.add(it);
        });

        // DB 저장 + 실패 시 Redis 보상 (불일치 방지)
        try {
            List<PostEntity> saved = postRepository.saveAll(postsToSave);
            return saved.size();
        } catch (RuntimeException e) {
            // 보상: 방금 추가했던 키들만 되돌림 (ZREM)
            for (PostEntity it : postsToSave) {
                storedKeyStore.removeStoredPostKey(it.getCommunityType(), it.getKey());
            }
            throw e; // 트랜잭션 롤백
        }
    }
}
