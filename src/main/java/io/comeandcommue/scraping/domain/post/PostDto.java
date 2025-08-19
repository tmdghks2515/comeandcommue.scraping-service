package io.comeandcommue.scraping.domain.post;

import io.comeandcommue.scraping.common.enums.CommunityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(builderMethodName = "of")
@AllArgsConstructor
public class PostDto {
    private String id;
    private String postNo;
    private String title;
    private String categoryName;
    private String linkHref;
    private String thumbnailSrc;
    private String authorName;
    private int likeCount;
    private int hitCount;
    private CommunityType communityType;
    private LocalDateTime postedAt;
    private LocalDateTime createdAt;
}
