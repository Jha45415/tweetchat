package oth.ics.wtp.tweetchatbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import oth.ics.wtp.tweetchatbackend.DTO.PostCreationDto;
import oth.ics.wtp.tweetchatbackend.entity.Post;
import oth.ics.wtp.tweetchatbackend.Service.PostService;
import oth.ics.wtp.tweetchatbackend.DTO.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createPost(@RequestPart("postData") PostCreationDto postDto,
                                        @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
                                        Authentication authentication) {
        String username = authentication.getName();
        try {

            Post newPost = postService.createPost(postDto, username, imageFile);
            PostDto responseDto = postService.mapPostToDto(newPost, null);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostDto>> getTimelineFeed(Authentication authentication,
                                                         @RequestParam(defaultValue = "0") int page) {
        String username = authentication.getName();
        List<PostDto> timeline = postService.getAggregatedTimeline(username,page);
        return ResponseEntity.ok(timeline);
    }


    @GetMapping("/user/{username}")
    public ResponseEntity<List<PostDto>> getPostsByUsername(@PathVariable String username,
                                                            Authentication authentication,
                                                            @RequestParam(defaultValue = "0") int page) {
        String currentUsername = authentication != null ? authentication.getName() : null;
        List<PostDto> posts = postService.getPostsByUsername(username, currentUsername, page);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId, Authentication authentication) {
        String username = authentication.getName();
        try {
            postService.likePost(postId, username);
            return ResponseEntity.ok().body("Post liked successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, Authentication authentication) {
        String username = authentication.getName();
        try {
            postService.unlikePost(postId, username);
            return ResponseEntity.ok().body("Post unliked successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, Authentication authentication) {
        String currentUsername = authentication.getName();
        try {
            postService.deletePost(postId, currentUsername);
            return ResponseEntity.ok().body("Post deleted successfully.");
        } catch (IllegalArgumentException e) {
            // This happens if the post or user doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            // This happens if the user is not the author
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}