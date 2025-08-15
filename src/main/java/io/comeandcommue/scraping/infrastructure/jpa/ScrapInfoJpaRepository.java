package io.comeandcommue.scraping.infrastructure.jpa;

import io.comeandcommue.scraping.common.ScrapTargetType;
import io.comeandcommue.scraping.domain.scrap.ScrapInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapInfoJpaRepository extends JpaRepository<ScrapInfoEntity, String> {
    List<ScrapInfoEntity> findAllByScrapTargetType(ScrapTargetType scrapTargetType);
}
