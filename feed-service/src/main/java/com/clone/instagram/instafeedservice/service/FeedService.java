package com.vibe.instafeedservice.service;

import com.vibe.instafeedservice.entity.UserFeed;
import com.vibe.instafeedservice.exception.ResourceNotFoundException;
import com.vibe.instafeedservice.model.Post;
import com.vibe.instafeedservice.payload.SlicedResult;
import com.vibe.instafeedservice.repository.FeedRepository;
import com.vibe.instafeedservice.util.AppConstants;
import com.datastax.driver.core.PagingState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.*;
import static java.util.stream.Collectors.toList;


@Service
@Slf4j
public class FeedService {

    @Autowired private FeedRepository feedRepository;
    @Autowired private PostService postService;
    @Autowired private AuthService authService;

    /*
    * 1 DB query + 2 remote service calls, Latency stacking problem.
    * Denormalizing feed projection.
    *
    * You generate JWT on EVERY feed request, Feed is usually the hottest endpoint in the system.
    * Token cache with TTL or Service-to-service internal auth
    *
    *
    * Profile pic enrichment = N+1 architecture smell
    * authService.usersProfilePic(token, usernames)  FeedService depends on AuthService schema.
    * profile metadata is usually embedded into feed projection.
    *
    * Throwing ResourceNotFoundException on empty feed is wrong semantics
    *
    *
    * a celebrity user has millions of feed entries Single partition becomes huge.
    *
    *
    * if PostService is down? Feed API fails completely.
    * Feed projection should be self-sufficient for basic rendering.
    *
    *
    * Why Cassandra instead of Mongo for feeds?
    * Wide-row partitioned time-series reads, PagingState support, High write throughput
    *
    * */

    public SlicedResult<Post> getUserFeed(String username, Optional<String> pagingState) {

        log.info("getting feed for user {} isFirstPage {}", username, pagingState.isEmpty());

        CassandraPageRequest request = pagingState
                .map(pState -> CassandraPageRequest
                        .of(PageRequest.of(0, AppConstants.PAGE_SIZE), PagingState.fromString(pState)))
                .orElse(CassandraPageRequest.first(AppConstants.PAGE_SIZE));

        Slice<UserFeed> page =
                feedRepository.findByUsername(username, request);

        if(page.isEmpty()) {
            throw new ResourceNotFoundException(
                    String.format("Feed not found for user %s", username));
        }

        String pageState = null;

        if(!page.isLast()) {
           pageState = ((CassandraPageRequest)page.getPageable())
                   .getPagingState().toString();
        }

        List<Post> posts = getPosts(page);

        return SlicedResult
                .<Post>builder()
                .content(posts)
                .isLast(page.isLast())
                .pagingState(pageState)
                .build();
    }

    private List<Post> getPosts(Slice<UserFeed> page) {

        String token = authService.getAccessToken();

        List<String> postIds = page.stream()
                .map(feed -> feed.getPostId())
                .collect(toList());

        List<Post> posts = postService.findPostsIn(token, postIds);

        List<String> usernames = posts.stream()
                .map(Post::getUsername)
                .distinct()
                .collect(toList());

        Map<String, String> usersProfilePics =
                authService.usersProfilePic(token, new ArrayList<>(usernames));

        posts.forEach(post -> post.setUserProfilePic(
                usersProfilePics.get(post.getUsername())));

        return posts;
    }
}
