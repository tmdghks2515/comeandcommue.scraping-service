package io.comeandcommue.scraping.domain.scrap;

import io.comeandcommue.scraping.common.NanoId;
import io.comeandcommue.scraping.common.ScrapProcessType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "scrap_process")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 프록시 생성을 위한 기본 생성자
@AllArgsConstructor // Builder와 같이 사용하면 편함
@Builder(builderMethodName = "of") // 생성자 대신 빌더 패턴 사용
//@ToString(exclude = {}) // 순환 참조 방지
@EqualsAndHashCode(of = "id", callSuper = false) // 엔티티 식별자로 equals/hashCode 정의
@EntityListeners(AuditingEntityListener.class)
public class ScrapProcessEntity {
    @Id
    @NanoId
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrap_property_id", nullable = false)
    private ScrapPropertyEntity scrapProperty;

    @Column(name = "scrap_process_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ScrapProcessType scrapProcessType;

    @Column(name = "replace_from")
    private String replaceFrom;

    @Column(name = "replace_to")
    private String replaceTo;

    @Column(name = "matcher_regex")
    private String matcherRegex;

    @Column(name = "matcher_group_no")
    private Integer matcherGroupNo;

    @Column(name = "split_str")
    private String splitStr;

    @Column(name = "split_index")
    private Integer splitIndex;

    @Column(name = "process_order", nullable = false)
    private int processOrder;
}
