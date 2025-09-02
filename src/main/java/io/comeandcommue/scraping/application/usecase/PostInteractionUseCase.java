package io.comeandcommue.scraping.application.usecase;

import io.comeandcommue.scraping.domain.post.PostEntity;
import io.comeandcommue.scraping.domain.post.PostLikeEntity;
import io.comeandcommue.scraping.domain.post.PostLikeId;
import io.comeandcommue.scraping.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class PostInteractionUseCase {
    private final PostRepository postRepository;

    public void hitPost(String postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));

        post.hitted();
        postRepository.save(post);
    }

    public boolean likePost(PostLikeId postLikeId) {
        PostEntity post = postRepository.findById(postLikeId.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));

        if (!postRepository.existsPostLikeById(postLikeId)) {
            PostLikeEntity postLike = PostLikeEntity.of()
                    .id(postLikeId)
                    .build();
            postRepository.savePostLike(postLike);
            post.liked();
            return true;
        } else {
            postRepository.deletePostLikeById(postLikeId);
            post.likeCanceled();
            return false;
        }
    }

    public void increaseCommentCount(String postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));

        post.commented();
        postRepository.save(post);
    }
}
