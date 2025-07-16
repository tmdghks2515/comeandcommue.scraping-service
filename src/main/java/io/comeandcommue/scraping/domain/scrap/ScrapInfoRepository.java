package io.comeandcommue.scraping.domain.scrap;

import io.comeandcommue.scraping.common.ScrapTargetType;

import java.util.List;

public interface ScrapInfoRepository {
    List<ScrapInfoEntity> findAllByScrapTargetType(ScrapTargetType scrapTargetType);
}
