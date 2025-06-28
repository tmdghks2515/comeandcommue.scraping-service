package io.comeandcommue.scraping.infrastructure.redis;

import io.comeandcommue.scraping.domain.scrap.StoredKeyStore;

import java.time.Duration;

public class RedisStoredKeyStore implements StoredKeyStore {
    private static final String STORED_KEY_KEY_FORMAT = "stored:%s:%s";

    @Override
    public boolean exists(String key) {
        return false;
    }

    @Override
    public void save(String key, Duration ttl) {

    }
}
