package io.comeandcommue.scraping.trigger;

import io.comeandcommue.lib.web.loginUser.LoginUser;
import io.comeandcommue.lib.web.loginUser.ResolvedLoginUser;
import io.comeandcommue.scraping.application.usecase.PostInteractionUseCase;
import io.comeandcommue.scraping.domain.post.PostLikeId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post/interaction")
@RequiredArgsConstructor
public class InteractionController {
    private final PostInteractionUseCase  postInteractionUseCase;

    @PostMapping("/{postId}/hit")
    public ResponseEntity<Void> hitPost(@PathVariable String postId) {
        postInteractionUseCase.hitPost(postId);
        return  ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Boolean> likePost(@PathVariable String postId, @ResolvedLoginUser LoginUser loginUser) {
        return  ResponseEntity.ok(
                postInteractionUseCase.likePost(new PostLikeId(postId, loginUser.id()))
        );
    }
}
