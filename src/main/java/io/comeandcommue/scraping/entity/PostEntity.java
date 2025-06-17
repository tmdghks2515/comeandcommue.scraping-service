package io.comeandcommue.scraping.entity;

import io.comeandcommue.scraping.util.NanoId;
import io.comeandcommue.scraping.vo.CommunityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 프록시 생성을 위한 기본 생성자
@AllArgsConstructor // Builder와 같이 사용하면 편함
@Builder(builderMethodName = "of") // 생성자 대신 빌더 패턴 사용
//@ToString(exclude = {}) // 순환 참조 방지
@EqualsAndHashCode(of = "id", callSuper = false) // 엔티티 식별자로 equals/hashCode 정의
public class PostEntity {
    @Id
    @NanoId
    private String id;

    @Column(name = "community_post_id")
    private String communityPostId;

    @Column(name = "title")
    private String title;

    @Column(name = "link_href", length = 1000)
    private String linkHref;

    @Column(name = "thumbnail_src", length = 1000)
    private String thumbnailSrc;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "comment_count")
    private Integer commentCount;

    @Column(name = "community_type")
    @Enumerated(EnumType.STRING)
    private CommunityType communityType;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
