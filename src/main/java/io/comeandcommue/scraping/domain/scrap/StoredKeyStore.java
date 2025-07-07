package io.comeandcommue.scraping.domain.scrap;


import io.comeandcommue.scraping.common.CommunityType;

public interface StoredKeyStore {
    boolean exists(CommunityType commuType, String key);
    void save(CommunityType commuType, String key);
}
