package io.comeandcommue.scraping.trigger;

import io.comeandcommue.scraping.application.ScrapHotPostsUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScrapingScheduler {
    private static final Logger log = LoggerFactory.getLogger(ScrapingScheduler.class);
    private final ScrapHotPostsUseCase scrapHotPostsUseCase;

    @Scheduled(cron = "0 * * * * *")
    public void scrapHotPosts() {
        log.info("scrapHotPosts started");
        long start = System.currentTimeMillis();
        int count = scrapHotPostsUseCase.scrapHotPosts();

        long end = System.currentTimeMillis();
        log.info("scrapHotPosts completed (took {} ms), scraped {} posts)", (end - start), count);
    }
}
