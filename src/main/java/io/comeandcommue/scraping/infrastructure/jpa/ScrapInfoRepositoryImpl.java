package io.comeandcommue.scraping.infrastructure.jpa;

import io.comeandcommue.scraping.common.enums.ScrapTargetType;
import io.comeandcommue.scraping.domain.scrap.ScrapInfoEntity;
import io.comeandcommue.scraping.domain.scrap.ScrapInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScrapInfoRepositoryImpl implements ScrapInfoRepository {
    private final ScrapInfoJpaRepository scrapInfoJpaRepository;

    @Override
    public List<ScrapInfoEntity> findAllByScrapTargetType(ScrapTargetType scrapTargetType) {
        return scrapInfoJpaRepository.findAllByScrapTargetType(scrapTargetType);
    }
}
