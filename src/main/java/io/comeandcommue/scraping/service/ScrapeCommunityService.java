package io.comeandcommue.scraping.service;

import io.comeandcommue.scraping.domain.PostDomainService;
import io.comeandcommue.scraping.dto.PostDto;
import io.comeandcommue.scraping.entity.PostEntity;
import io.comeandcommue.scraping.mapper.PostMapper;
import io.comeandcommue.scraping.repository.PostRepository;
import io.comeandcommue.scraping.vo.CommunityType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ScrapeCommunityService {
    private static final Logger log = LoggerFactory.getLogger(ScrapeCommunityService.class);

    private final PostDomainService postDomainService;
    private final PostRepository postRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;

    private static final String LATEST_SCRAPED_COMMU_TYPE = "posts:commutype";
    private static final String LATEST_POSTS_KEY = "posts:latest";
    private static final String COMMU_TYPE_POST_IDS_KEY_FORMAT = "posts:ids:%s";
    private static final String LATEST_COMMU_TYPE_POSTS_KEY_FORMAT = "posts:latest:%s";

    public void scrapeByCommuType() {
        ValueOperations<String, String> stringValueOps = stringRedisTemplate.opsForValue();

        LocalDateTime now = LocalDateTime.now();

        String latestCommuType =  stringValueOps.get(LATEST_SCRAPED_COMMU_TYPE);
        CommunityType commuType = latestCommuType == null || latestCommuType.isEmpty()
                ? CommunityType.DCINSIDE // Start with DCINSIDE if no previous type
                : CommunityType.fromOrder(CommunityType.fromName(latestCommuType).getOrder() + 1);
        stringValueOps.set(LATEST_SCRAPED_COMMU_TYPE, commuType.name());

        log.info("Starting scraping for community type: {}", commuType);

        List<PostDto> posts = switch (commuType) {
            case DCINSIDE -> postDomainService.scrapeDcinsidePost();
            case FMKOREA -> throw new UnsupportedOperationException("FMKOREA scraping not implemented yet");
            case THEQOO -> throw new UnsupportedOperationException("THEQOO scraping not implemented yet");
        };

        if (posts.isEmpty()) return;

        List<PostEntity> postsToSave = new ArrayList<>();

        for (PostDto post : posts) {
            Boolean result = stringRedisTemplate.opsForZSet().add(
                    COMMU_TYPE_POST_IDS_KEY_FORMAT.formatted(commuType.name()),
                    post.getCommunityPostId(),
                    now.toEpochSecond(ZoneOffset.UTC)
            );
            if (Boolean.FALSE.equals(result)) {
                log.info("Post {} already exists in Redis, skipping", post.getCommunityPostId());
                continue; // Skip if post already exists
            }

            // Save to Redis
            redisTemplate.opsForZSet().add(
                    LATEST_POSTS_KEY,
                    post,
                    now.toEpochSecond(ZoneOffset.UTC)
            );
            redisTemplate.opsForZSet().add(
                    LATEST_COMMU_TYPE_POSTS_KEY_FORMAT.formatted(commuType.name()),
                    post,
                    now.toEpochSecond(ZoneOffset.UTC)
            );
            postsToSave.add(PostMapper.toEntity(post));
        }

        // Save to Database
        if (!postsToSave.isEmpty()) {
            postRepository.saveAll(postsToSave);
        }
    }

    public void deleteExpiredCache() {
        ZSetOperations<String, Object> zsetOps = redisTemplate.opsForZSet();

        long epochSeconds7Days = Instant.now().minus(Duration.ofDays(7)).getEpochSecond();
        long epochSeconds30Days = Instant.now().minus(Duration.ofDays(30)).getEpochSecond();

        zsetOps.removeRangeByScore(LATEST_POSTS_KEY, 0, epochSeconds7Days);

        for (CommunityType commuType : CommunityType.values()) {
            String commuPostsKey = LATEST_COMMU_TYPE_POSTS_KEY_FORMAT.formatted(commuType.name());
            String commuPostIdsKey = COMMU_TYPE_POST_IDS_KEY_FORMAT.formatted(commuType.name());

            zsetOps.removeRangeByScore(commuPostsKey, 0, epochSeconds7Days);
            stringRedisTemplate.opsForZSet().removeRangeByScore(commuPostIdsKey, 0, epochSeconds30Days);
        }
    }
}
