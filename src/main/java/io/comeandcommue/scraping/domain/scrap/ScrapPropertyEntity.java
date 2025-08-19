package io.comeandcommue.scraping.domain.scrap;

import io.comeandcommue.scraping.common.enums.ExtractMethod;
import io.comeandcommue.scraping.common.nanoId.NanoId;
import io.comeandcommue.scraping.common.enums.PostPropertyType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "scrap_property")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 프록시 생성을 위한 기본 생성자
@AllArgsConstructor // Builder와 같이 사용하면 편함
@Builder(builderMethodName = "of") // 생성자 대신 빌더 패턴 사용
@ToString(exclude = {"scrapProcesses"}) // 순환 참조 방지
@EqualsAndHashCode(of = "id", callSuper = false) // 엔티티 식별자로 equals/hashCode 정의
@EntityListeners(AuditingEntityListener.class)
public class ScrapPropertyEntity {
    @Id
    @NanoId
    private String id;

    @ManyToOne
    @JoinColumn(name = "scrap_info_id", nullable = false)
    private ScrapInfoEntity scrapInfo;

    @Column(name = "post_property_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostPropertyType postPropertyType;

    @Column(name = "selector", nullable = false)
    private String selector;

    @Column(name = "select_idx")
    private int selectIdx = 0;

    @Column(name = "extract_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExtractMethod extractMethod = ExtractMethod.TEXT;

    @Column(name = "attr_name")
    private String attrName;

    @Column(name = "ad_detection_regex")
    private String adDetectionRegex;

    @Column(name = "property_skip_detection_regex")
    private String propertySkipDetectionRegex;

    @Column(name = "date_format")
    private String dateFormat;

    @Column(name = "date_format_2")
    private String dateFormat2;

    @OneToMany(mappedBy = "scrapProperty", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("processOrder ASC")
    private List<ScrapProcessEntity> scrapProcesses = new ArrayList<>();
}
