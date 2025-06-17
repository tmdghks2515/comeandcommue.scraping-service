package io.comeandcommue.scraping.repository;


import io.comeandcommue.scraping.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
