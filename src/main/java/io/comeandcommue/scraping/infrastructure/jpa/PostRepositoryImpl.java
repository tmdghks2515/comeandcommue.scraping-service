package io.comeandcommue.scraping.infrastructure.jpa;

import io.comeandcommue.scraping.domain.post.PostEntity;
import io.comeandcommue.scraping.domain.post.PostLikeEntity;
import io.comeandcommue.scraping.domain.post.PostLikeId;
import io.comeandcommue.scraping.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostJpaRepository postJpaRepository;
    private final PostLikeJpaEntity postLikeJpaEntity;

    /************************ PostEntity ********************************/
    @Override
    public Optional<PostEntity> findById(String id) {
        return postJpaRepository.findById(id);
    }

    @Override
    public PostEntity save(PostEntity postEntity) {
        return  postJpaRepository.save(postEntity);
    }

    @Override
    public List<PostEntity> saveAll(List<PostEntity> postEntityList) {
        return  postJpaRepository.saveAll(postEntityList);
    }

    /************************ PostLikeEntity ********************************/
    @Override
    public boolean existsPostLikeById(PostLikeId id) {
        return postLikeJpaEntity.existsById(id);
    }

    @Override
    public PostLikeEntity savePostLike(PostLikeEntity postLikeEntity) {
        return postLikeJpaEntity.save(postLikeEntity);
    }

    @Override
    public void deletePostLikeById(PostLikeId id) {
        postLikeJpaEntity.deleteById(id);
    }
}
