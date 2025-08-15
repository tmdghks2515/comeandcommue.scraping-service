package io.comeandcommue.scraping.infrastructure.jpa;

import io.comeandcommue.scraping.domain.post.PostLikeEntity;
import io.comeandcommue.scraping.domain.post.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeJpaEntity extends JpaRepository<PostLikeEntity, PostLikeId> {
}
