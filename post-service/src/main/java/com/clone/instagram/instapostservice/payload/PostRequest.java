package com.vibe.instapostservice.payload;

import lombok.Data;

// request dto

@Data
public class PostRequest {

    private String imageUrl;
    private String caption;
}
