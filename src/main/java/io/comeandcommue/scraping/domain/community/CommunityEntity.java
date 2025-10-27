package io.comeandcommue.scraping.domain.community;

import io.comeandcommue.lib.data.baseEntity.BaseEntity;
import io.comeandcommue.scraping.common.enums.CommunityType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "community")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 프록시 생성을 위한 기본 생성자
@AllArgsConstructor // Builder와 같이 사용하면 편함
@Builder(builderMethodName = "of") // 생성자 대신 빌더 패턴 사용
//@ToString(exclude = {}) // 순환 참조 방지
@EqualsAndHashCode(of = "communityType", callSuper = false) // 엔티티 식별자로 equals/hashCode 정의
@EntityListeners(AuditingEntityListener.class)
public class CommunityEntity extends BaseEntity {
    @Id
    @Column(name = "community_type")
    @Enumerated(EnumType.STRING)
    private CommunityType communityType;

    @Column(name = "base_url")
    private String baseUrl;

    @Column(name = "post_base_url")
    private String postBaseUrl;

    @Column(name = "has_img_permission")
    private boolean hasImgPermission;

    @Column(name = "use_selenium")
    private boolean useSelenium;
}
