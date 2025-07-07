

package oth.ics.wtp.tweetchatbackend.Service;

import oth.ics.wtp.tweetchatbackend.DTO.UserRegistrationDto;
import oth.ics.wtp.tweetchatbackend.entity.User;
import oth.ics.wtp.tweetchatbackend.Repository.UserRepository;
import oth.ics.wtp.tweetchatbackend.DTO.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + registrationDto.getUsername());
        }
        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        return userRepository.save(newUser);
    }

    @Transactional
    public void followUser(String currentUsername, String usernameToFollow) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found."));
        User userToFollow = userRepository.findByUsername(usernameToFollow)
                .orElseThrow(() -> new IllegalArgumentException("User to follow not found."));
        currentUser.followUser(userToFollow);
    }

    @Transactional
    public void unfollowUser(String currentUsername, String usernameToUnfollow) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found."));
        User userToUnfollow = userRepository.findByUsername(usernameToUnfollow)
                .orElseThrow(() -> new IllegalArgumentException("User to unfollow not found."));
        if (!currentUser.getFollowing().contains(userToUnfollow)) {
            throw new IllegalArgumentException("You are not following this user.");
        }
        currentUser.unfollowUser(userToUnfollow);
    }




    @Transactional(readOnly = true)
    public List<UserDto> searchUsers(String searchTerm, String currentUsername) {

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Collections.emptyList();
        }


        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found."));

        List<User> foundUsers = userRepository.findByUsernameContainingIgnoreCase(searchTerm.trim());

        return foundUsers.stream()
                .filter(user -> !user.getUsername().equals(currentUsername))
                .map(user -> mapUserToDto(user, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto getUserProfile(String profileUsername, String currentUsername) {
        User profileUser = userRepository.findByUsername(profileUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + profileUsername));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found."));

        return mapUserToDto(profileUser, currentUser); // Use the helper mapper
    }

    /**
     * Private helper method to map a User entity to a UserDto.
     * This method is called from within other @Transactional methods,
     * so it has access to the database session needed for lazy loading.
     */
    private UserDto mapUserToDto(User userToMap, User currentUser) {
        UserDto dto = new UserDto();
        dto.setId(userToMap.getId());
        dto.setUsername(userToMap.getUsername());


        dto.setFollowedByCurrentUser(currentUser.getFollowing().contains(userToMap));
        dto.setFollowersCount(userToMap.getFollowers().size());
        dto.setFollowingCount(userToMap.getFollowing().size());


        return dto;
    }



    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }




    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(String username, String currentUsername) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found."));


        return user.getFollowing().stream()
                .map(followedUser -> mapUserToDto(followedUser, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(String username, String currentUsername) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found."));


        return user.getFollowers().stream()
                .map(follower -> mapUserToDto(follower, currentUser))
                .collect(Collectors.toList());
    }
}