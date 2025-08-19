package io.comeandcommue.scraping.infrastructure.redis;

import io.comeandcommue.scraping.common.enums.CommunityType;
import io.comeandcommue.scraping.domain.scrap.StoredKeyStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisStoredKeyStore implements StoredKeyStore {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String STORED_KEY_KEY_FORMAT = "stored:%s:%s"; // stored:{commuType}:{postNo}

    @Override
    public boolean exists(CommunityType commuType, String key) {
        String fullKey = String.format(STORED_KEY_KEY_FORMAT, commuType.name().toLowerCase(), key);
        return redisTemplate.hasKey(fullKey);
    }

    @Override
    public void save(CommunityType commuType, String key) {
        String fullKey = String.format(STORED_KEY_KEY_FORMAT, commuType.name().toLowerCase(), key);
        redisTemplate.opsForValue().set(fullKey, "1", Duration.ofDays(5)); // Store for 5 days
    }
}
