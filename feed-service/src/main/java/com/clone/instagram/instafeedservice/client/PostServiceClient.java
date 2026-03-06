package com.vibe.Vibefeedservice.client;

import com.vibe.Vibefeedservice.model.Post;
import com.vibe.Vibefeedservice.payload.JwtAuthenticationResponse;
import com.vibe.Vibefeedservice.payload.ServiceLoginRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@FeignClient(serviceId = "Vibe-POST")
public interface PostServiceClient {

    @RequestMapping(method = RequestMethod.POST, value = "/posts/in")
    ResponseEntity<List<Post>> findPostsByIdIn(
            @RequestHeader("Authorization") String token,
            @RequestBody List<String> ids);

}
