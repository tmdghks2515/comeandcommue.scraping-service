package io.comeandcommue.scraping.domain.scrap;

import io.comeandcommue.scraping.common.CommunityType;
import io.comeandcommue.scraping.domain.post.PostEntity;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class ScrapService {
    private static final Logger log = LoggerFactory.getLogger(ScrapService.class);

    public List<PostEntity> scrapPostsByCommuType(CommunityType commuType) {
        return switch(commuType) {
            case DCINSIDE -> scrapDcinsidePosts();
            case FMKOREA -> scrapFmkoreaPosts();
            case THEQOO -> scrapTheqooPosts();
            case PPOMPPU -> scrapPpomppuPosts();
            case RULIWEB -> scrapRuliwebPosts();
            case BOBAEDREAM -> scrapBobaedreamPosts();
            case INSTIZ -> scrapInstizPosts();
            default -> new ArrayList<>();
        };
    }

    public List<PostEntity> scrapDcinsidePosts() {
        List<PostEntity> posts = new ArrayList<>();
        String scrapTargetUrl = "https://gall.dcinside.com/board/lists/?id=dcbest";

        try {
            Document doc = makeJsoupConnection(scrapTargetUrl);
            Elements rows = doc.select("table.gall_list > tbody > tr.us-post");

            if (rows.isEmpty()) {
                return posts;
            }

            for (Element row : rows) {
                try {
                    Element postNoElement = row.selectFirst("td.gall_num");
                    String postNo = (postNoElement != null) ? postNoElement.text().trim() : null;

                    Element hrefElement = row.selectFirst("td.gall_tit a");
                    String href = hrefElement != null ? hrefElement.attr("href") : null;

                    Element titleElement = row.selectFirst("td.gall_tit > a");
                    String title = titleElement != null ? titleElement.text().trim() : null;

                    // 필수값 체크
                    if (href == null || title == null) {
                        continue;
                    }

                    Pattern pattern = Pattern.compile("\\[(.*?)]");
                    Matcher matcher = pattern.matcher(title);

                    String categoryName = null;
                    if (matcher.find()) {
                        categoryName = matcher.group(1); // 매칭된 대괄호 안의 문자열
                    }

                    Element thumbElement = row.selectFirst("td.gall_tit > a > div.thumimg > img");
                    boolean hasImg = thumbElement != null;

                    Element authorNameElement = row.selectFirst("td.gall_writer > span.nickname");
                    String authorName = authorNameElement != null ? authorNameElement.attr("title") : null;

                    Element postedAtElement = row.selectFirst("td.gall_date");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime postedAt = postedAtElement != null ? LocalDateTime.parse(postedAtElement.attr("title"), formatter) : null;

                    Element viewCountElement = row.selectFirst("td.gall_count");
                    Integer viewCount = viewCountElement != null ? Integer.parseInt(viewCountElement.text().trim()) : null;

                    Element likeCountElement = row.selectFirst("td.gall_recommend");
                    Integer likeCount = likeCountElement != null ? Integer.parseInt(likeCountElement.text().trim()) : null;

                    posts.add(
                            PostEntity.of()
                                    .postNo(postNo)
                                    .title(title)
                                    .categoryName(categoryName)
                                    .linkHref(href)
                                    .authorName(authorName)
                                    .hasImg(hasImg)
                                    .viewCount(viewCount)
                                    .likeCount(likeCount)
                                    .communityType(CommunityType.DCINSIDE)
                                    .postedAt(postedAt)
                                    .build()
                    );
                } catch (Exception e) {
                    // 예외 발생 시 해당 row는 무시하고 다음 row로 넘어감
                    log.debug("Error processing dcinside row: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error connecting to dcinside: {}", e.getMessage());
        }

        return posts;
    }

    public List<PostEntity> scrapFmkoreaPosts() {
        List<PostEntity> posts = new ArrayList<>();
        String scrapTargetUrl = "https://www.fmkorea.com";

        try {
            Document doc = makeJsoupConnection(scrapTargetUrl);
            Elements rows = doc.select("div.fm_best_widget > ul > li.li_best2_politics0");

            if (rows.isEmpty()) {
                return posts;
            }

            for (Element row : rows) {
                try {
                    Element titleElement = row.selectFirst("div.li > h3.title > a");
                    String href = titleElement != null ? titleElement.attr("href") : null;
                    String title = titleElement != null ? titleElement.text().trim() : null;

                    // 필수값 체크
                    if (href == null || title == null) {
                        continue;
                    }

                    Element thumnailElement = row.selectFirst("div.li > a > img.thumb");
                    String thumnailSrc = thumnailElement != null ? thumnailElement.attr("src") : null;
                    boolean hasImg = thumnailElement != null;

                    Element authorElement = row.selectFirst("div.li > div > span.author");
                    String authorName = authorElement != null ? authorElement.text().replace("/", "").trim() : null;

                    Element categoryElement = row.selectFirst("div.li > div > span.category");
                    String categoryName = categoryElement != null ? categoryElement.text().trim() : null;

                    Element commentCountElement = row.selectFirst("div.li > h3.title > a > span.comment_count");
                    Integer commentCount = commentCountElement != null
                            ? Integer.parseInt(
                                    commentCountElement.text()
                                            .replaceAll("\\[", "")
                                            .replaceAll("]", "")
                                            .trim()
                            ) : null;

                    Element likeCountElement = row.selectFirst("div.li > a.pc_voted_count > span.count");
                    Integer likeCount = likeCountElement != null ? Integer.parseInt(likeCountElement.text().trim()) : null;

                    posts.add(
                            PostEntity.of()
                                    .title(title)
                                    .linkHref(href)
                                     .thumbnailSrc(thumnailSrc)
                                    .hasImg(hasImg)
                                    .authorName(authorName)
                                    .categoryName(categoryName)
                                    .commentCount(commentCount)
                                    .likeCount(likeCount)
                                    .communityType(CommunityType.FMKOREA)
                                    .build()
                    );
                } catch (Exception e) {
                    // 예외 발생 시 해당 row는 무시하고 다음 row로 넘어감
                    log.debug("Error processing fmkorea row: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error connecting to fmkorea: {}", e.getMessage());
        }

        return posts;
    }

    public List<PostEntity> scrapTheqooPosts() {
        List<PostEntity> posts = new ArrayList<>();
        String scrapTargetUrl = "https://theqoo.net/hot";

        try {
            Document doc = makeJsoupConnection(scrapTargetUrl);
            Elements rows = doc.select("div.bd_load_target > div.bd_lst_wrp > table.theqoo_board_table > tbody > tr:not(.notice)");

            if (rows.isEmpty()) {
                return posts;
            }

            for (Element row : rows) {
                try {
                     Element postNoElement = row.selectFirst("td.no");
                     String postNo = postNoElement != null ? postNoElement.text().trim() : null;

                    Element titleElement = row.selectFirst("td.title > a");
                    String title = titleElement != null ? titleElement.text().trim() : null;
                    String href = titleElement != null ? titleElement.attr("href") : null;

                    // 필수값 체크
                    if (href == null || title == null) {
                        continue;
                    }

                    Element thumnailElement = row.selectFirst("td.title > i.fa-images");
                    boolean hasImg = thumnailElement != null;

                    Element categoryElement = row.selectFirst("td.cate > span");
                    String categoryName = categoryElement != null ? categoryElement.text().trim() : null;

                    Element commentCountElement = row.selectFirst("td.title > a.replyNum");
                    Integer commentCount = commentCountElement != null
                            ? Integer.parseInt(commentCountElement.text().replaceAll(",", "").trim())
                            : null;

                    Element viewCountElement = row.selectFirst("td.m_no");
                    Integer viewCount = viewCountElement != null
                            ? Integer.parseInt(viewCountElement.text().replaceAll(",", "").trim())
                            : null;

                    posts.add(
                            PostEntity.of()
                                     .postNo(postNo)
                                    .title(title)
                                    .linkHref(href)
                                    .hasImg(hasImg)
                                    .categoryName(categoryName)
                                    .commentCount(commentCount)
                                    .viewCount(viewCount)
                                    .communityType(CommunityType.THEQOO)
                                    .build()
                    );
                } catch (Exception e) {
                    // 예외 발생 시 해당 row는 무시하고 다음 row로 넘어감
                    log.debug("Error processing theqoo row: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error connecting to theqoo: {}", e.getMessage());
        }

        return posts;
    }

    public List<PostEntity> scrapPpomppuPosts() {
        List<PostEntity> posts = new ArrayList<>();
        String scrapTargetUrl = "https://ppomppu.co.kr/hot.php";

        try {
            Document doc = makeJsoupConnection(scrapTargetUrl);
            Elements rows = doc.select("div.board_box > table.board_table > tbody > tr.bbs_new1");

            if (rows.isEmpty()) {
                return posts;
            }

            for (Element row : rows) {
                try {
                    Elements titleElements = row.select("td.title a.baseList-title");
                    String title = null;
                    String href = null;
                    for (Element el : titleElements) {
                        if (!el.text().trim().isEmpty()) {
                            title = el.text().trim();
                            href = el.attr("href");
                        }
                    }

                    // 필수값 체크
                    if (href == null || title == null) {
                        continue;
                    }

                    Element thumnailElement = row.selectFirst("td.title a.baseList-thumb img");
                    String thumbnailSrc = thumnailElement != null ? thumnailElement.attr("src") : null;
                    if (thumbnailSrc != null && thumbnailSrc.contains("noimage"))
                        thumbnailSrc = null;
                    boolean hasImg = thumbnailSrc != null;



                    Element authorElement = row.selectFirst("td.baseList-space > div.list_name");
                    String authorName = authorElement != null ? authorElement.text().trim() : null;

                    Element categoryElement = row.selectFirst("td.baseList-numb > a");
                    String categoryName = categoryElement != null ? categoryElement.text().trim() : null;

                    // 광고 및 스폰서 게시글 제외
                    if (categoryName != null && categoryName.contains("스폰서")) {
                        continue;
                    }

                    Element commentCountElement = row.selectFirst("td.title span.list_commend2");
                    Integer commentCount = commentCountElement != null
                            ? Integer.parseInt(commentCountElement.text().replaceAll(",", "").trim())
                            : null;

                    Elements lastElements = row.select("td.board_date");
                    Integer likeCount = null;
                    Integer viewCount = null;
                    LocalDateTime postedAt = null;
                    for (int i = 0; i < lastElements.size(); i++) {
                        Element el = lastElements.get(i);
                        if (i == 0) {
                            String datetimeStr = el.text();
                            if (datetimeStr.contains(":")) {
                                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                                LocalTime time = LocalTime.parse(datetimeStr, timeFormatter);
                                LocalDate today = LocalDate.now();

                                postedAt = LocalDateTime.of(today, time);
                            } else if (datetimeStr.contains("/")) {
                                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yy/MM/dd");
                                LocalDate date = LocalDate.parse(datetimeStr, timeFormatter);
                                LocalTime time = LocalTime.of(0, 0);

                                postedAt = LocalDateTime.of(date, time);
                            }
                        } else if (i == 1) {
                            likeCount = Integer.parseInt(el.text().split("-")[0].trim());
                        } else if (i == 2) {
                            viewCount = Integer.parseInt(el.text().replaceAll(",", "").trim());
                        }
                    }

                    if (lastElements.size() >= 3) {
                        Element viewCountElement = lastElements.get(2);
                        viewCount = Integer.parseInt(viewCountElement.text().replaceAll(",", "").trim());
                    }

                    posts.add(
                            PostEntity.of()
                                    .title(title)
                                    .authorName(authorName)
                                    .linkHref(href)
                                    .hasImg(hasImg)
                                    .thumbnailSrc(thumbnailSrc)
                                    .categoryName(categoryName)
                                    .commentCount(commentCount)
                                    .viewCount(viewCount)
                                    .likeCount(likeCount)
                                    .communityType(CommunityType.PPOMPPU)
                                    .postedAt(postedAt)
                                    .build()
                    );
                } catch (Exception e) {
                    // 예외 발생 시 해당 row는 무시하고 다음 row로 넘어감
                    log.debug("Error processing ppomppu row: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error connecting to ppomppu: {}", e.getMessage());
        }

        return posts;
    }

    public List<PostEntity> scrapRuliwebPosts() {
        List<PostEntity> posts = new ArrayList<>();
        String scrapTargetUrl = "https://bbs.ruliweb.com/best/humor?orderby=best_id";

        try {
            Document doc = makeJsoupConnection(scrapTargetUrl);
            Elements rows = doc.select("#best_body tr.mode_list");

            if (rows.isEmpty()) {
                return posts;
            }

            for (Element row : rows) {
                try {
                    // 게시글 번호
                    String postNo = row.selectFirst("td.id").text().trim();

                    // 제목과 링크
                    Element linkElement = row.selectFirst("td.subject > a.subject_link");
                    String href = linkElement != null ? linkElement.attr("href") : null;

                    Element titleElement = linkElement != null ? linkElement.selectFirst("span.text_over") : null;
                    String title = titleElement != null ? titleElement.text().trim() : null;

                    // 필수값 검사
                    if (href == null || title == null|| title.startsWith("AD")) {
                        continue;
                    }

                    // 카테고리 추출 (예: [블루아카] or 블루아카) → 대괄호 없이도 포함 가능
                    String categoryName = null;
                    Matcher matcher = Pattern.compile("([\\[(]?)([가-힣a-zA-Z0-9_]+)[\\])]?\\)").matcher(title);
                    if (matcher.find()) {
                        categoryName = matcher.group(2);
                    }

                    // 이미지 유무: 루리웹 베스트에는 목록에 썸네일 없음 → false 고정
                    boolean hasImg = false;

                    // 작성자
                    String authorName = row.selectFirst("td.writer") != null
                            ? row.selectFirst("td.writer").text().trim()
                            : null;

                    // 댓글 수
                    Integer commentCount = null;
                    Element replyElement = row.selectFirst("span.num_reply");
                    if (replyElement != null) {
                        Matcher replyMatcher = Pattern.compile("\\((\\d+)\\)").matcher(replyElement.text());
                        if (replyMatcher.find()) {
                            commentCount = Integer.parseInt(replyMatcher.group(1));
                        }
                    }

                    // 추천 수
                    Integer likeCount = null;
                    Element likeElement = row.selectFirst("td.recomd");
                    if (likeElement != null) {
                        likeCount = Integer.parseInt(likeElement.text().trim());
                    }

                    // 조회 수
                    Integer viewCount = null;
                    Element viewElement = row.selectFirst("td.hit");
                    if (viewElement != null) {
                        viewCount = Integer.parseInt(viewElement.text().trim());
                    }

                    // 등록 시각 (형식이 "HH:mm" 또는 "yy.MM.dd")
                    String rawTime = row.selectFirst("td.time") != null ? row.selectFirst("td.time").ownText().trim() : null;
                    LocalDateTime postedAt = null;

                    if (rawTime != null) {
                        if (rawTime.contains(":")) {
                            // 시간만 있을 경우 → 오늘 날짜로 보정
                            LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                            LocalTime time = LocalTime.parse(rawTime, formatter);
                            postedAt = LocalDateTime.of(today, time);
                        } else if (rawTime.matches("\\d{2}\\.\\d{2}\\.\\d{2}")) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
                            postedAt = LocalDate.parse(rawTime, formatter).atStartOfDay();
                        }
                    }

                    posts.add(
                            PostEntity.of()
                                    .postNo(postNo)
                                    .title(title)
                                    .categoryName(categoryName)
                                    .linkHref(href)
                                    .authorName(authorName)
                                    .hasImg(hasImg)
                                    .viewCount(viewCount)
                                    .likeCount(likeCount)
                                    .commentCount(commentCount)
                                    .communityType(CommunityType.RULIWEB)
                                    .postedAt(postedAt)
                                    .build()
                    );
                } catch (Exception e) {
                    log.debug("Error processing ruliweb row: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error connecting to ruliweb: {}", e.getMessage());
        }

        return posts;
    }

    public List<PostEntity> scrapBobaedreamPosts() {
        List<PostEntity> posts = new ArrayList<>();
        String scrapTargetUrl = "https://www.bobaedream.co.kr/list?code=best"; // 정확한 핫글 목록 주소로 수정 필요

        try {
            Document doc = makeJsoupConnection(scrapTargetUrl);
            Elements rows = doc.select("table#boardlist > tbody > tr[itemtype='http://schema.org/Article']");

            if (rows.isEmpty()) return posts;

            for (Element row : rows) {
                try {
                    // 카테고리
                    String categoryName = row.selectFirst("td.category") != null
                            ? row.selectFirst("td.category").text().trim()
                            : null;

                    // 제목
                    Element titleElement = row.selectFirst("td.pl14 a.bsubject");
                    String title = titleElement != null ? titleElement.text().trim() : null;

                    // 링크
                    String href = titleElement != null ? titleElement.attr("href") : null;

                    // 댓글 수
                    Integer commentCount = null;
                    Element commentStrong = row.selectFirst("td.pl14 .Comment > strong.totreply");
                    if (commentStrong != null) {
                        commentCount = Integer.parseInt(commentStrong.text().trim());
                    }

                    // 작성자
                    String authorName = row.selectFirst("td.author02 span.author") != null
                            ? row.selectFirst("td.author02 span.author").text().trim()
                            : null;

                    // 등록 시간 (HH:mm 또는 오늘 날짜 기준)
                    String rawTime = row.selectFirst("td.date") != null
                            ? row.selectFirst("td.date").text().trim()
                            : null;
                    LocalDateTime postedAt = null;
                    if (rawTime != null && rawTime.matches("\\d{2}:\\d{2}")) {
                        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
                        LocalTime time = LocalTime.parse(rawTime, DateTimeFormatter.ofPattern("HH:mm"));
                        postedAt = LocalDateTime.of(today, time);
                    }

                    // 추천 수
                    Integer likeCount = null;
                    Element likeElement = row.selectFirst("td.recomm font[style]");
                    if (likeElement != null) {
                        likeCount = Integer.parseInt(likeElement.text().trim());
                    }

                    // 조회 수
                    Integer viewCount = null;
                    Element hitElement = row.selectFirst("td.count");
                    if (hitElement != null) {
                        String viewStr = hitElement.text().replace(",", "").replaceAll("[^0-9]", "");
                        viewCount = Integer.parseInt(viewStr);
                    }

                    // 이미지 유무: 게시글 목록에는 썸네일 아이콘(img.jpg 등)으로 존재
                    boolean hasImg = row.selectFirst("td.pl14 img.jpg") != null;

                    // 필수값 확인
                    if (href == null || title == null) continue;

                    posts.add(
                            PostEntity.of()
                                    .postNo(null)
                                    .title(title)
                                    .categoryName(categoryName)
                                    .linkHref(href)
                                    .authorName(authorName)
                                    .hasImg(hasImg)
                                    .viewCount(viewCount)
                                    .likeCount(likeCount)
                                    .commentCount(commentCount)
                                    .communityType(CommunityType.BOBAEDREAM)
                                    .postedAt(postedAt)
                                    .build()
                    );
                } catch (Exception e) {
                    log.debug("Error processing bobaedream row: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Error connecting to bobaedream: {}", e.getMessage());
        }

        return posts;
    }

    public List<PostEntity> scrapInstizPosts() {
        List<PostEntity> posts = new ArrayList<>();
        String scrapTargetUrl = "https://www.instiz.net/hot.htm";

        try {
            Document doc = makeJsoupConnection(scrapTargetUrl);
            Elements rows = doc.select("div.result_search");

            if (rows.isEmpty()) return posts;

            for (Element row : rows) {
                try {
                    // //www.instiz.net/pt/7752234
                    Element anchor = row.selectFirst("a[href]");
                    String href = anchor != null ? anchor.attr("href").trim() : null;
                    if (href != null) {
                        href = href.replace("//www.instiz.net", "");
                    }

                    // 제목
                    String title = row.selectFirst("span.search_title") != null
                            ? row.selectFirst("span.search_title").text().trim()
                            : null;

                    // 카테고리
                    String categoryName = row.selectFirst("div.text > span.minitext2") != null
                            ? row.selectFirst("div.text > span.minitext2").text().trim()
                            : null;

                    // 댓글 수
                    Integer commentCount = null;
                    Element replyElement = row.selectFirst("span.cmt2");
                    if (replyElement != null) {
                        commentCount = Integer.parseInt(replyElement.text().trim());
                    }

                    // 시간
                    String rawTime = row.select("span.minitext3").size() > 0
                            ? row.select("span.minitext3").get(0).text().trim()
                            : null;
                    LocalDateTime postedAt = null;
                    if (rawTime != null && rawTime.matches("\\d{2}:\\d{2}")) {
                        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
                        LocalTime time = LocalTime.parse(rawTime, DateTimeFormatter.ofPattern("HH:mm"));
                        postedAt = LocalDateTime.of(today, time);
                    }

                    // 조회 수
                    Integer viewCount = null;
                    if (row.select("span.minitext3").size() > 1) {
                        String viewText = row.select("span.minitext3").get(1).text();
                        Matcher matcher = Pattern.compile("조회\\s*(\\d+)").matcher(viewText);
                        if (matcher.find()) {
                            viewCount = Integer.parseInt(matcher.group(1));
                        }
                    }

                    // 썸네일 URL 추출
                    String thumbnailSrc = null;
                    Element img = row.selectFirst("div.thumb img");
                    if (img != null) {
                        thumbnailSrc = img.hasAttr("data-original")
                                ? img.attr("data-original").trim()
                                : img.attr("src").trim();
                    }

                    boolean hasImg = thumbnailSrc != null;

                    if (href == null || title == null) continue;

                    posts.add(
                            PostEntity.of()
                                    .postNo(null)
                                    .title(title)
                                    .categoryName(categoryName)
                                    .linkHref(href)
                                    .authorName(null) // 작성자 없음
                                    .hasImg(hasImg)
                                    .thumbnailSrc(thumbnailSrc)
                                    .viewCount(viewCount)
                                    .likeCount(null) // 추천 없음
                                    .commentCount(commentCount)
                                    .communityType(CommunityType.INSTIZ)
                                    .postedAt(postedAt)
                                    .build()
                    );
                } catch (Exception e) {
                    log.debug("Error parsing instiz post row: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Error connecting to instiz: {}", e.getMessage());
        }

        return posts;
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
