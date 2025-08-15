package io.comeandcommue.scraping.trigger;

import io.comeandcommue.scraping.application.PostInteractionUseCase;
import io.comeandcommue.scraping.common.LoginUser;
import io.comeandcommue.scraping.common.UserPrincipal;
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

    @PostMapping("/hit/{postId}")
    public ResponseEntity<Void> hitPost(@PathVariable String postId) {
        postInteractionUseCase.hitPost(postId);
        return  ResponseEntity.ok().build();
    }

    @PostMapping("/like/{postId}")
    public ResponseEntity<Void> likePost(@PathVariable String postId, @LoginUser UserPrincipal user) {
        postInteractionUseCase.likePost(new PostLikeId(postId, user.id()));
        return  ResponseEntity.ok().build();
    }
}
