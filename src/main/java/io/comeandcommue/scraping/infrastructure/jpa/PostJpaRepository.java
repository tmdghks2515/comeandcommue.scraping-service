package io.comeandcommue.scraping.infrastructure.jpa;

import io.comeandcommue.scraping.domain.post.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<PostEntity, String> {
}
