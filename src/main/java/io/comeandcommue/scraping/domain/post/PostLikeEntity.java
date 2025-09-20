package io.comeandcommue.scraping.domain.post;

import io.comeandcommue.lib.data.baseEntity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "post_like",
        indexes = {
                // @Index(name = "idx_post_like_user_created", columnList = "user_id, created_at"), // "내가 좋아요 한 글" 조회 많다면 추가
                // @Index(name = "idx_post_like_post", columnList = "post_id") // "게시글 별 좋아요한 사람들" 조회 많을시 추가
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "of")
@Getter
public class PostLikeEntity extends BaseTimeEntity {

    @EmbeddedId
    private PostLikeId id;
}
