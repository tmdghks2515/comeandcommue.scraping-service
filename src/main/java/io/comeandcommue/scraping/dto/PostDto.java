package io.comeandcommue.scraping.dto;

import io.comeandcommue.scraping.vo.CommunityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(builderMethodName = "of")
@AllArgsConstructor
public class PostDto {
    private String id;
    private String communityPostId;
    private String title;
    private String linkHref;
    private String thumbnailSrc;
    private String authorName;
    private Integer likeCount;
    private Integer viewCount;
    private Integer commentCount;
    private CommunityType communityType;
    private LocalDateTime postedAt;
    private LocalDateTime createdAt;
}
