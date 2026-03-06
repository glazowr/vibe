package com.vibe.authservice.messaging;

import com.vibe.authservice.model.User;
import com.vibe.authservice.payload.UserEventPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class UserEventSender {

    private UserEventStream channels;

    public UserEventSender(UserEventStream channels) {
        this.channels = channels;
    }

    public void sendUserCreated(User user) {
        log.info("sending user created event for user {}", user.getUsername());
        sendUserChangedEvent(convertTo(user, UserEventType.CREATED));
    }

    public void sendUserUpdated(User user) {
        log.info("sending user updated event for user {}", user.getUsername());
        sendUserChangedEvent(convertTo(user, UserEventType.UPDATED));
    }

    public void sendUserUpdated(User user, String oldPicUrl) {
        log.info("sending user updated (profile pic changed) event for user {}",
                user.getUsername());

        UserEventPayload payload = convertTo(user, UserEventType.UPDATED);
        payload.setOldProfilePicUrl(oldPicUrl);

        sendUserChangedEvent(payload);
    }

    private void sendUserChangedEvent(UserEventPayload payload) {

        Message<UserEventPayload> message =
                MessageBuilder
                        .withPayload(payload)
                        // Kafka partitioning will be based on userId.
                        // All events for the same user land in the same partition. Order is preserved per user
                        .setHeader(KafkaHeaders.MESSAGE_KEY, payload.getId())
                        .build();

        // Use transactional outbox or reliable event publishing strategy.
        channels.momentsUserChanged().send(message);

        log.info("user event {} sent to topic {} for user {}",
                message.getPayload().getEventType().name(),
                channels.OUTPUT,
                message.getPayload().getUsername());
    }


    // event schema flat instead of nested UserProfile object to avoid schema drift and reduce coupling
    private UserEventPayload convertTo(User user, UserEventType eventType) {
        return UserEventPayload
                .builder()
                .eventType(eventType)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                // leaking domain structure into event schema
                .displayName(user.getUserProfile().getDisplayName())
                .profilePictureUrl(user.getUserProfile().getProfilePictureUrl()).build();
    }
}
