package io.comeandcommue.scraping.domain;

import io.comeandcommue.scraping.dto.PostDto;
import io.comeandcommue.scraping.entity.PostEntity;
import io.comeandcommue.scraping.vo.CommunityType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class PostDomainService {
    public List<PostDto> scrapeDcinsidePost() {
        List<PostDto> posts = new ArrayList<>();
        String exampleUrl = "https://gall.dcinside.com/board/lists/?id=dcbest&list_num=100";

        try {
            Document doc = Jsoup.connect(exampleUrl)
                    .userAgent("Mozilla/5.0") // User-Agent 설정 필수
                    .get();

            Elements rows = doc.select("table.gall_list tbody tr.us-post");

            if (rows.isEmpty()) {
                return posts;
            }

            for (Element row : rows) {
                try {
                    Element numElement = row.selectFirst("td.gall_num");
                    String communityPostId = (numElement != null) ? numElement.text() : null;

                    Element hrefElement = row.selectFirst("td.gall_tit a");
                    String href = hrefElement != null ? hrefElement.attr("href") : null;

                    /* 이미지 접근 권한 없음으로 썸네일 저장 안함
                    Element thumnailElement = row.selectFirst("td.gall_tit div.thumimg img");
                    String thumnailSrc = thumnailElement != null ? thumnailElement.attr("src") : null; */

                    Element titleElement = row.selectFirst("td.gall_tit a");
                    String title = titleElement != null ? titleElement.text() : null;

                    Element authorNameElement = row.selectFirst("td.gall_writer span.nickname");
                    String authorName = authorNameElement != null ? authorNameElement.attr("title") : null;

                    Element postedAtElement = row.selectFirst("td.gall_date");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime postedAt = postedAtElement != null ? LocalDateTime.parse(postedAtElement.attr("title"), formatter) : null;

                    Element viewCountElement = row.selectFirst("td.gall_count");
                    Integer viewCount = viewCountElement != null ? Integer.parseInt(viewCountElement.text()) : null;

                    Element likeCountElement = row.selectFirst("td.gall_recommend");
                    Integer likeCount = likeCountElement != null ? Integer.parseInt(likeCountElement.text()) : null;

                    posts.add(
                            PostDto.of()
                                    .communityPostId(communityPostId)
                                    .title(title)
                                    .linkHref(href)
                                    // .thumbnailSrc(thumnailSrc)
                                    .authorName(authorName)
                                    .viewCount(viewCount)
                                    .likeCount(likeCount)
                                    .communityType(CommunityType.DCINSIDE)
                                    .postedAt(postedAt)
                                    .createdAt(LocalDateTime.now())
                                    .build()
                    );
                } catch (Exception e) {
                    // 예외 발생 시 해당 row는 무시하고 다음 row로 넘어감
                }
            }
            return posts;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
