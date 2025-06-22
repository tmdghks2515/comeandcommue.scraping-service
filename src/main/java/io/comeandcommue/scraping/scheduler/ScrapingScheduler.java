package io.comeandcommue.scraping.scheduler;

import io.comeandcommue.scraping.service.ScrapeCommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScrapingScheduler {
    private  final ScrapeCommunityService scrapeCommunityService;

    @Scheduled(cron = "0 * * * * *")
    public void scrapeNextCommunity() {
        scrapeCommunityService.scrapeByCommuType();
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void deleteExpiredCache() {
        scrapeCommunityService.deleteExpiredCache();
    }
}
