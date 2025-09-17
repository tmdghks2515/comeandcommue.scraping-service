package io.comeandcommue.scraping.trigger;

import io.comeandcommue.scraping.application.usecase.PostInteractionUseCase;
import io.comeandcommue.lib.web.loginUser.LoginUser;
import io.comeandcommue.scraping.domain.post.PostLikeId;
import io.comeandcommue.lib.web.loginUser.UserPrincipal;
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
    public ResponseEntity<Boolean> likePost(@PathVariable String postId, @LoginUser UserPrincipal user) {
        return  ResponseEntity.ok(
                postInteractionUseCase.likePost(new PostLikeId(postId, user.id()))
        );
    }
}
