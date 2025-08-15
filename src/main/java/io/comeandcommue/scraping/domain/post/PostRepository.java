package io.comeandcommue.scraping.domain.post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    /************************ PostEntity ********************************/
    Optional<PostEntity> findById(String id);
    PostEntity save(PostEntity postEntity);
    List<PostEntity> saveAll(List<PostEntity> postEntityList);

    /************************ PostLikeEntity ********************************/
    boolean existsPostLikeById(PostLikeId id);
    PostLikeEntity savePostLike(PostLikeEntity postLikeEntity);
    void deletePostLikeById(PostLikeId id);
}
