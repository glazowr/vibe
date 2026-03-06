package com.vibe.instapostservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;


// response dto
@Data
@AllArgsConstructor
public class ApiResponse {

    private Boolean success;
    private String message;
}