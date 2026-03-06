package com.vibe.instapostservice.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Whenever auditing is needed, I will provide a String representing the current user.
// This username will be inserted into @CreatedBy @LastModifiedBy automatically
// // UserAuditing is a bridge between Spring Security and JPA auditing — it converts “current authenticated user” into “database audit metadata”.

// Spring Data calls getCurrentAuditor() every time:
// a Post is saved
// a Comment is updated
// any audited entity changes


@Component
public class UserAuditing implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.of(username);
    }
}
