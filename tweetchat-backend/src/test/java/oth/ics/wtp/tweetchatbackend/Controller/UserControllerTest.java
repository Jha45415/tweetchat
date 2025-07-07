package oth.ics.wtp.tweetchatbackend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import oth.ics.wtp.tweetchatbackend.DTO.UserLoginDto;
import oth.ics.wtp.tweetchatbackend.entity.User;
import oth.ics.wtp.tweetchatbackend.Repository.UserRepository;
import oth.ics.wtp.tweetchatbackend.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    private User userA;
    private User userB;
    private String tokenA;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        userA = new User();
        userA.setUsername("userA");
        userA.setPassword(passwordEncoder.encode("passwordA"));
        userA = userRepository.save(userA);

        userB = new User();
        userB.setUsername("userB");
        userB.setPassword(passwordEncoder.encode("passwordB"));
        userB = userRepository.save(userB);

        tokenA = loginAndGetToken("userA", "passwordA");
    }

    @Test
    void followUser_whenValid_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/users/{username}/follow", "userB")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());
    }

    @Test
    void searchUsers_shouldReturnUserList() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("q", "userB")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("userB")));
    }

    @Test
    void getUserProfile_shouldReturnProfile() throws Exception {
        mockMvc.perform(get("/api/users/{username}", "userB")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("userB")));
    }

    @Test
    void unfollowUser_whenValid_shouldReturnOk() throws Exception {
        userService.followUser("userA", "userB");

        mockMvc.perform(post("/api/users/{username}/unfollow", "userB")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Successfully unfollowed userB"));

        User updatedUserA = userRepository.findByUsername("userA").get();
        assertFalse(updatedUserA.getFollowing().contains(userB));
    }

    @Test
    void getFollowers_shouldReturnUserList() throws Exception {
        userService.followUser("userB", "userA");

        mockMvc.perform(get("/api/users/{username}/followers", "userA")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("userB")));
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername(username);
        loginDto.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseString).get("accessToken").asText();
    }
    @Test
    void followUser_whenTargetUserNotFound_shouldReturnBadRequest() throws Exception {

        String nonExistentUsername = "ghost_user";


        mockMvc.perform(post("/api/users/{username}/follow", nonExistentUsername)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User to follow not found."));
    }
    @Test
    void unfollowUser_whenNotFollowing_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/users/{username}/unfollow", "userB")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isBadRequest());
    }
}