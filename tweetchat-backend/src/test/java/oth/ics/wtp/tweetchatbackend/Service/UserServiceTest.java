package oth.ics.wtp.tweetchatbackend.Service;

import oth.ics.wtp.tweetchatbackend.DTO.UserRegistrationDto;
import oth.ics.wtp.tweetchatbackend.entity.User;
import oth.ics.wtp.tweetchatbackend.Repository.UserRepository;
import oth.ics.wtp.tweetchatbackend.DTO.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        userA = new User();
        userA.setUsername("userA");
        userA.setPassword(passwordEncoder.encode("passwordA"));
        userA = userRepository.save(userA);

        userB = new User();
        userB.setUsername("userB");
        userB.setPassword(passwordEncoder.encode("passwordB"));
        userB = userRepository.save(userB);
    }

    @Test
    void registerNewUser_shouldSaveUserWithHashedPassword() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("newUser");
        dto.setPassword("newPassword");

        User savedUser = userService.registerNewUser(dto);
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        assertNotNull(foundUser);
        assertEquals("newUser", foundUser.getUsername());
        assertTrue(passwordEncoder.matches("newPassword", foundUser.getPassword()));
    }

    @Test
    void registerNewUser_whenUsernameExists_shouldThrowException() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("userA"); // This user already exists from setUp
        dto.setPassword("somePassword");

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerNewUser(dto);
        });
    }

    @Test
    void followUser_shouldCreateRelationship() {
        userService.followUser("userA", "userB");

        User foundUserA = userRepository.findByUsername("userA").get();
        User foundUserB = userRepository.findByUsername("userB").get();

        assertTrue(foundUserA.getFollowing().contains(foundUserB));
        assertTrue(foundUserB.getFollowers().contains(foundUserA));
    }

    @Test
    void unfollowUser_shouldRemoveRelationship() {
        userService.followUser("userA", "userB");
        userService.unfollowUser("userA", "userB");

        User foundUserA = userRepository.findByUsername("userA").get();
        User foundUserB = userRepository.findByUsername("userB").get();

        assertFalse(foundUserA.getFollowing().contains(foundUserB));
        assertFalse(foundUserB.getFollowers().contains(foundUserA));
    }

    @Test
    void searchUsers_shouldReturnCorrectDto() {
        userService.followUser("userA", "userB");

        List<UserDto> results = userService.searchUsers("userB", "userA");

        assertEquals(1, results.size());
        assertEquals("userB", results.get(0).getUsername());
        assertTrue(results.get(0).isFollowedByCurrentUser());
    }
}