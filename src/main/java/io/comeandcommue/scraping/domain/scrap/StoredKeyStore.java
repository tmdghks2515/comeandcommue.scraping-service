package io.comeandcommue.scraping.domain.scrap;

import java.time.Duration;

public interface StoredKeyStore {
    boolean exists(String key);
    void save(String key, Duration ttl);
}
