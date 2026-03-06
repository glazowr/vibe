package com.vibe.authservice.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;


//Cloud Stream decouples broker implementation and allows functional binding,
// partition config, and reactive streams without changing producer logic.

public interface UserEventStream {

    String OUTPUT = "momentsUserChanged";

    @Output(OUTPUT)
    MessageChannel momentsUserChanged();
}
