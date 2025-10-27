package io.comeandcommue.scraping.infrastructure.redis;

import io.comeandcommue.scraping.common.enums.CommunityType;
import io.comeandcommue.scraping.domain.scrap.StoredPostStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
public class RedisStoredKeyStore implements StoredPostStore {

    private static final long FIVE_DAYS_MS = 5L * 24 * 60 * 60 * 1000;

    private final RedisTemplate<String, String> redisTemplate;

    private static final String STORED_KEY_KEY_FORMAT = "stored:%s"; // stored:{commuType}

    @Override
    public boolean saveStoredPostKey(CommunityType commuType, String key) {
        String lua = """
            local key    = KEYS[1]
            local member = ARGV[1]
            local score  = tonumber(ARGV[2])
            local cutoff = tonumber(ARGV[3])
    
            -- NX: 없을 때만 추가 (중복 방지)
            local added = redis.call('ZADD', key, 'NX', score, member)
            -- 오래된 멤버 정리 (슬라이딩 윈도우)
            redis.call('ZREMRANGEBYSCORE', key, '-inf', cutoff)
            return added or 0
        """;
        RedisScript<Long> redisScript = RedisScript.of(lua, Long.class);

        long now = Instant.now().toEpochMilli();
        String fullKey = String.format(STORED_KEY_KEY_FORMAT, commuType.name().toLowerCase());
        long cutoff = now - FIVE_DAYS_MS;
        Long added = redisTemplate.execute(
                redisScript,
                Collections.singletonList(fullKey),
                key, String.valueOf(now), String.valueOf(cutoff)
        );

        return added == 1L;
    }

    @Override
    public void removeStoredPostKey(CommunityType commuType, String key) {
        String fullKey = String.format(STORED_KEY_KEY_FORMAT, commuType.name().toLowerCase());
        redisTemplate.opsForZSet().remove(fullKey, key);
    }
}
