
package oth.ics.wtp.tweetchatbackend.controller;

import oth.ics.wtp.tweetchatbackend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import oth.ics.wtp.tweetchatbackend.DTO.UserDto;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<?> followUser(@PathVariable String username, Authentication authentication) {
        String currentUsername = authentication.getName();
        if (currentUsername.equals(username)) {
            return ResponseEntity.badRequest().body("You cannot follow yourself.");
        }

        try {
            userService.followUser(currentUsername, username);
            return ResponseEntity.ok().body("Successfully followed " + username);
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{username}/unfollow")
    public ResponseEntity<?> unfollowUser(@PathVariable String username, Authentication authentication) {
        String currentUsername = authentication.getName();
        try {
            userService.unfollowUser(currentUsername, username);
            return ResponseEntity.ok().body("Successfully unfollowed " + username);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam("q") String query, Authentication authentication) {
        String currentUsername = authentication.getName();
        // We need a new service method that returns DTOs
        List<UserDto> users = userService.searchUsers(query, currentUsername);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable String username, Authentication authentication) {
        String currentUsername = authentication.getName();
        UserDto userProfile = userService.getUserProfile(username, currentUsername);
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<List<UserDto>> getFollowing(@PathVariable String username, Authentication authentication) {
        String currentUsername = authentication.getName();
        List<UserDto> followingList = userService.getFollowing(username, currentUsername);
        return ResponseEntity.ok(followingList);
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable String username, Authentication authentication) {
        String currentUsername = authentication.getName();
        List<UserDto> followerList = userService.getFollowers(username, currentUsername);
        return ResponseEntity.ok(followerList);
    }


}
