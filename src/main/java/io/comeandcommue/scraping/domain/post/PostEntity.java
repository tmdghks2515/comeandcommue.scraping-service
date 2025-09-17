package io.comeandcommue.scraping.domain.post;

import io.comeandcommue.scraping.common.nanoId.NanoId;
import io.comeandcommue.scraping.common.enums.CommunityType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 프록시 생성을 위한 기본 생성자
@AllArgsConstructor // Builder와 같이 사용하면 편함
@Builder(builderMethodName = "of") // 생성자 대신 빌더 패턴 사용
//@ToString(exclude = {}) // 순환 참조 방지
@EqualsAndHashCode(of = "id", callSuper = false) // 엔티티 식별자로 equals/hashCode 정의
@EntityListeners(AuditingEntityListener.class)
public class PostEntity {
    @Id
    @NanoId
    private String id;

    @Column(name = "post_no")
    private String postNo;

    @Column(name = "title")
    private String title;

    @Column(name = "category_name")
    private String categoryName;

    @NonNull
    @Column(name = "link_href", length = 1000, nullable = false)
    private String linkHref;

    @Column(name = "thumbnail_src", length = 1000)
    private String thumbnailSrc;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "like_count")
    private int likeCount;

    @Builder.Default
    @Column(name = "hit_count")
    private int hitCount = 0;

    @Builder.Default
    @Column(name = "comment_count")
    private int commentCount = 0;

    @Column(name = "community_type", columnDefinition = "varchar(100)")
    @Enumerated(EnumType.STRING)
    private CommunityType communityType;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public String getKey() {
        return postNo != null ? postNo : linkHref;
    }

    public void hitted() {
        hitCount++;
    }

    public void liked() {
        likeCount++;
    }

    public void likeCanceled() {
        likeCount--;
    }

    public void commented() {
        commentCount++;
    }
}
