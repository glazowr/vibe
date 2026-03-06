package com.clone.instagram.instafeedservice.messaging;

import com.clone.instagram.instafeedservice.model.Post;
import com.clone.instagram.instafeedservice.payload.PostEventPayload;
import com.clone.instagram.instafeedservice.service.FeedGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostEventListener {

    private FeedGeneratorService feedGeneratorService;

    public PostEventListener(FeedGeneratorService feedService) {
        this.feedGeneratorService = feedService;
    }

    @StreamListener(PostEventStream.INPUT)
    public void onMessage(Message<PostEventPayload> message) {

        PostEventType eventType = message.getPayload().getEventType();

        log.info("received message to process post {} for user {} eventType {}",
                message.getPayload().getId(),
                message.getPayload().getUsername(),
                eventType.name());
        // Why manual acknowledgment instead of auto?
        // We want offset commit only after feed materialization succeeds to prevent data loss during consumer crashes.

        // How do you prevent duplicate feed entries if Kafka retries?
        // Upsert by (postId + followerId) OR EventId-based deduplication (need to do)

        // Manual acknowledgment without error handling is dangerous
        // classic idempotency problem.
        Acknowledgment acknowledgment =
                message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);

        // Missing DLQ / retry strategy
        // Post CREATED arrives before Post DELETED. Are you validating event timestamps or versions?


        // should be EventHandler Strategy Pattern not Switch-case smells like premature design
        switch (eventType) {
            case CREATED:
                feedGeneratorService.addToFeed(convertTo(message.getPayload()));
                break;
            case DELETED:
                break;
        }

        if(acknowledgment != null) {
            acknowledgment.acknowledge();
        }
    }


    // Domain reconstruction from Event Payload — architectural smell
    // Right now your FeedService is implicitly coupled to PostService. We should introduce a FeedProjection model to decouple schemas.
    private Post convertTo(PostEventPayload payload) {
        return Post
                .builder()
                .id(payload.getId())
                .createdAt(payload.getCreatedAt())
                .username(payload.getUsername())
                .build();
    }
}
