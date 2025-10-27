package io.comeandcommue.scraping.domain.scrap;


import io.comeandcommue.scraping.common.enums.CommunityType;


public interface StoredPostStore {
    boolean saveStoredPostKey(CommunityType commuType, String key);
    void removeStoredPostKey(CommunityType commuType, String key);
}
