package io.comeandcommue.scraping.application.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.comeandcommue.scraping.application.usecase.PostInteractionUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class SubscriberConfig {
    private final ObjectMapper objectMapper;
    private final Logger log =  LoggerFactory.getLogger(SubscriberConfig.class);

    @Bean
    public RedisMessageListenerContainer listenerContainer(
            LettuceConnectionFactory connectionFactory,
            MessageListener commentCreatedListener,
            ChannelTopic commentCreatedTopic
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(commentCreatedListener, commentCreatedTopic);
        return container;
    }

    @Bean
    public MessageListener commentCreatedListener(
            PostInteractionUseCase useCase
    ) {
        return (message, pattern) -> {
            String postId = null;
            try {
                // 1) 메시지 본문을 UTF-8 문자열로 변환
                postId = new String(message.getBody(), StandardCharsets.UTF_8);

                // 3) 필수값 검증
                if (postId == null || postId.isBlank()) {
                    log.warn("Invalid PostCommentCreatedEvent payload: {}", postId);
                    return;
                }

                // 4) 동기 비즈니스 처리
                useCase.increaseCommentCount(postId);

            } catch (Exception e) {
                // 5) 에러 로깅 (채널/패턴 정보 포함)
                String channel = message.getChannel() != null
                        ? new String(message.getChannel(), StandardCharsets.UTF_8)
                        : "unknown";
                String pat = pattern != null ? new String(pattern, StandardCharsets.UTF_8) : "none";

                log.error("Failed to handle commentCreated message. channel={}, pattern={}, payload={}",
                        channel, pat, postId, e);

                // TODO: 필요 시 DLQ/보류 큐로 json 이동 (예: Redis List/Stream/Kafka 등)
            }
        };
    }
}
