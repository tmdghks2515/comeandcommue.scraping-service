package io.comeandcommue.scraping.domain.scrap;

import io.comeandcommue.scraping.domain.post.PostEntity;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ScrapService {
    private static final Logger log = LoggerFactory.getLogger(ScrapService.class);

    public List<PostEntity> scrapRealtimeHotPosts(List<ScrapInfoEntity> scrapInfoList) {
        List<PostEntity> posts = new ArrayList<>();

        for (ScrapInfoEntity scrapInfo : scrapInfoList ) {
            posts.addAll(scrapByScrapInfo(scrapInfo));
        }

        return posts;
    }

    private List<PostEntity> scrapByScrapInfo(ScrapInfoEntity scrapInfo) {
        List<PostEntity> posts = new ArrayList<>();
        try {
            Document doc = makeJsoupConnection(scrapInfo.getTargetUrl());
            Elements rows = doc.select(scrapInfo.getTargetRowSelector());

            if (rows.isEmpty()) {
                log.debug(
                        "[ScrapService.scrapByScrapInfo] No rows found for scrap url {}, targetRowSelector {}",
                        scrapInfo.getTargetUrl(),
                        scrapInfo.getTargetRowSelector()
                );
                return posts;
            }

            for (Element row : rows) {
                try {
                    String postNo = null;
                    String title = null;
                    String categoryName = null;
                    String linkHref = null;
                    String thumbnailSrc = null;
                    String authorName = null;
                    Integer likeCount = null;
                    Integer viewCount = null;
                    Integer commentCount = null;
                    LocalDateTime postedAt = null;

                    for (ScrapPropertyEntity property : scrapInfo.getScrapProperties()) {
                        String extractedValue = extractValue(row, property);
                        switch (property.getPostPropertyType()) {
                            case POST_NO -> postNo = extractedValue;
                            case TITLE -> title = extractedValue;
                            case CATEGORY_NAME -> categoryName = extractedValue;
                            case AUTHOR_NAME ->  authorName = extractedValue;
                            case LINK_HREF -> {
                                if (extractedValue == null) break;
                                String commuBaseUrl = scrapInfo.getCommunity().getBaseUrl();
                                if (!extractedValue.contains(commuBaseUrl))
                                    linkHref = commuBaseUrl + extractedValue;
                                else
                                    linkHref = extractedValue;
                            }
                            case THUMBNAIL_SRC -> thumbnailSrc = extractedValue;
                            case LIKE_COUNT -> {
                                if (extractedValue != null) {
                                    likeCount = Integer.parseInt(extractedValue);
                                }
                            }
                            case VIEW_COUNT -> {
                                if (extractedValue != null) {
                                    viewCount = Integer.parseInt(extractedValue);
                                }
                            }
                            case COMMENT_COUNT -> {
                                if (extractedValue != null) {
                                    commentCount = Integer.parseInt(extractedValue);
                                }
                            }
                            case POSTED_AT -> {
                                if (extractedValue != null && property.getDateFormat() != null) {
                                    postedAt = formatDateTime(extractedValue, property.getDateFormat());
                                    if (postedAt == null && property.getDateFormat2() != null) {
                                        postedAt = formatDateTime(extractedValue, property.getDateFormat2());
                                    }
                                }
                            }
                        }
                    }

                    if (title == null || linkHref == null) {
                        log.debug(
                                "Skipping post due to missing required fields: title={}, linkHref={}",
                                title, linkHref
                        );
                        continue; // 필수값이 없으면 해당 row는 무시
                    }

                    posts.add(
                            PostEntity.of()
                                    .postNo(postNo)
                                    .title(title)
                                    .categoryName(categoryName)
                                    .linkHref(linkHref)
                                    .authorName(authorName)
                                    .thumbnailSrc(thumbnailSrc)
                                    .viewCount(viewCount)
                                    .likeCount(likeCount)
                                    .commentCount(commentCount)
                                    .communityType(scrapInfo.getCommunity().getCommunityType())
                                    .postedAt(postedAt)
                                    .build()
                    );

                } catch (Exception e) {
                    log.debug("Error processing row: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Error connecting to {}: {}", scrapInfo.getCommunity().getCommunityType(), e.getMessage());
        }

        return posts;
    }

    private String extractValue(Element row, ScrapPropertyEntity property) {
        Elements elmts = row.select(property.getSelector());
        if (elmts.isEmpty() || elmts.size() < property.getSelectIdx() + 1)
            return null;

        Element propertyElement = elmts.get(property.getSelectIdx());

        String value = switch (property.getExtractMethod()) {
            case ATTR ->  propertyElement.attr(property.getAttrName());
            case TEXT ->  propertyElement.text();
            case OWN_TEXT ->   propertyElement.ownText();
        };
        if (value.isEmpty()) {
            return null;
        }

        String processedValue = value;
        if (!property.getScrapProcesses().isEmpty()) {
            for (ScrapProcessEntity process : property.getScrapProcesses()) {
                processedValue = processingValue(processedValue, process);
            }
        }

        if (property.getPropertySkipDetectionRegex() != null) {
            Matcher matcher = Pattern.compile(property.getPropertySkipDetectionRegex()).matcher(processedValue);
            if (matcher.find()) {
                return null;
            }
        }

        if (property.getAdDetectionRegex() != null) {
            Matcher adMatcher = Pattern.compile(property.getAdDetectionRegex()).matcher(processedValue);
            if (adMatcher.find()) {
                throw new IllegalArgumentException("Detected ad content: " + processedValue);
            }
        }

        return processedValue.trim();
    }

    private String processingValue(String originValue, ScrapProcessEntity process) {
        String processedValue = originValue;
        switch (process.getScrapProcessType()) {
            case REPLACE -> {
                if (process.getReplaceFrom() != null && process.getReplaceTo() != null) {
                    processedValue = processedValue.replace(process.getReplaceFrom(), process.getReplaceTo());
                }
            }
            case REPLACE_ALL -> {
                if (process.getReplaceFrom() != null && process.getReplaceTo() != null) {
                    processedValue = processedValue.replaceAll(process.getReplaceFrom(), process.getReplaceTo());
                }
            }
            case MATCHER -> {
                if (process.getMatcherRegex() != null) {
                    Matcher matcher = Pattern.compile(process.getMatcherRegex()).matcher(processedValue);
                    if (matcher.find()) {
                        processedValue = matcher.group(process.getMatcherGroupNo());
                    } else {
                        processedValue = null; // 매칭 실패 시 null 처리
                    }
                }
            }
            case SPLIT -> {
                if (process.getSplitStr() != null && process.getSplitIndex() != null) {
                    String[] parts = processedValue.split(process.getSplitStr());
                    if (parts.length > process.getSplitIndex()) {
                        processedValue = parts[process.getSplitIndex()];
                    } else {
                        processedValue = null; // 인덱스 초과 시 null 처리
                    }
                }
            }
        }

        return processedValue;
    }

    private LocalDateTime formatDateTime(String value, String format) {
        LocalDateTime datetime = null;
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

            // 날짜/시간 모두 존재
            if ((format.contains("D") || format.contains("d")) && (format.contains("H") || format.contains("h"))) {
                datetime = LocalDateTime.parse(value, formatter)
                        .withYear(today.getYear());
            }
            // 시간만 존재
            else if (format.contains("H") || format.contains("h")) {
                LocalTime time = LocalTime.parse(value, formatter);
                datetime = LocalDateTime.of(today, time);
            }
            // 날짜만 존재
            else if (format.contains("D") || format.contains("d")) {
                datetime = LocalDate.parse(value, formatter)
                        .withYear(today.getYear())
                        .atStartOfDay();
            }
            else throw new IllegalArgumentException();
        } catch (RuntimeException e) {
            log.info("Error formatting date: {}, format: {}", value, format);
        }

        return datetime;
    }

    private Document makeJsoupConnection(String url) throws IOException {
        // 1. Selenium으로 브라우저 열기
//        WebDriver driver = new ChromeDriver();
//        driver.get(url);
//
//        // 2. 쿠키 수집 (JS 실행 이후 자동 설정된 쿠키 포함)
//        Set<Cookie> seleniumCookies = driver.manage().getCookies();
//        Map<String, String> cookieMap = seleniumCookies.stream()
//                .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
//                .cookies(cookieMap)
                .referrer("https://www.google.com/")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                .header("Accept-Language", "ko,en-US;q=0.9,en;q=0.8")
                .header("Connection", "keep-alive")
                .timeout(10000)
                .method(Connection.Method.GET)
                .get();

//        driver.quit();

        return doc;
    }
}
