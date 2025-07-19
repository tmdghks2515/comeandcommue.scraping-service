package io.comeandcommue.scraping.domain.scrap;

import io.comeandcommue.scraping.common.NanoId;
import io.comeandcommue.scraping.common.ScrapTargetType;
import io.comeandcommue.scraping.domain.community.CommunityEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "scrap_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 프록시 생성을 위한 기본 생성자
@AllArgsConstructor // Builder와 같이 사용하면 편함
@Builder(builderMethodName = "of") // 생성자 대신 빌더 패턴 사용
@ToString(exclude = {"scrapProperties"}) // 순환 참조 방지
@EqualsAndHashCode(of = "id", callSuper = false) // 엔티티 식별자로 equals/hashCode 정의
@EntityListeners(AuditingEntityListener.class)
public class ScrapInfoEntity {
    @Id
    @NanoId
    private String id;

    @ManyToOne
    @JoinColumn(name = "community_type", nullable = false)
    private CommunityEntity community;

    @Column(name = "scrap_target_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ScrapTargetType scrapTargetType;

    @Column(name = "target_url", nullable = false)
    private String targetUrl;

    @Column(name = "target_row_selector", nullable = false)
    private String targetRowSelector;

    @OneToMany(mappedBy = "scrapInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ScrapPropertyEntity> scrapProperties = new ArrayList<>();
}
