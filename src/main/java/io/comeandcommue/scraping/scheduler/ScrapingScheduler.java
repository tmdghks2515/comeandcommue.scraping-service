package io.comeandcommue.scraping.scheduler;

import io.comeandcommue.scraping.service.ScrapeCommunityService;
import io.comeandcommue.scraping.vo.CommunityType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScrapingScheduler {
    private  final ScrapeCommunityService scrapeCommunityService;
    private int currentCommunityOrder = 1; // Start with the first community

    @Scheduled(cron = "0 * * * * *")
    public void scrapeNextCommunity() {
        CommunityType currentType = CommunityType.fromOrder(currentCommunityOrder);
        scrapeCommunityService.scrapeByCommunityType(currentType);

        currentCommunityOrder++;

        // Reset order
        if (currentCommunityOrder > CommunityType.values().length) {
            currentCommunityOrder = 1;
        }
    }
}
