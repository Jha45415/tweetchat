package oth.ics.wtp.tweetchatbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import oth.ics.wtp.tweetchatbackend.DTO.AuthTokenDto;
import oth.ics.wtp.tweetchatbackend.DTO.PostCreationDto;
import oth.ics.wtp.tweetchatbackend.DTO.UserLoginDto;
import oth.ics.wtp.tweetchatbackend.DTO.UserRegistrationDto;
import oth.ics.wtp.tweetchatbackend.Repository.PostRepository;
import oth.ics.wtp.tweetchatbackend.Repository.UserRepository;
import oth.ics.wtp.tweetchatbackend.Service.PostService;
import oth.ics.wtp.tweetchatbackend.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void userRegistrationAndLoginAndPostCreation_shouldWorkEndToEnd() throws Exception {
        String token = registerAndLogin("integration_user", "password123");
        assertTrue(userRepository.findByUsername("integration_user").isPresent());

        long postId = createPostAsUser("This is a post", token);
        assertEquals(1, postRepository.count());
        assertTrue(postRepository.findById(postId).isPresent());
    }

    @Test
    void fullUserJourney_RegisterLoginPostFollowLike_shouldSucceed() throws Exception {
        String tokenA = registerAndLogin("userA", "passwordA");
        String tokenB = registerAndLogin("userB", "passwordB");
        assertEquals(2, userRepository.count());

        mockMvc.perform(post("/api/users/userB/follow")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());

        long postId = createPostAsUser("A post by userB", tokenB);
        assertEquals(1, postRepository.count());

        mockMvc.perform(get("/api/posts/feed")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorUsername").value("userB"));

        mockMvc.perform(post("/api/posts/{postId}/like", postId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());

        var likedPost = postRepository.findById(postId).get();
        assertEquals(1, likedPost.getLikesCount());
    }

    @Test
    void login_withInvalidCredentials_shouldReturnForbidden() throws Exception {
        registerAndLogin("test_user", "correct_password");

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("test_user");
        loginDto.setPassword("wrong_password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void register_whenUsernameAlreadyExists_shouldReturnBadRequest() throws Exception {
        registerAndLogin("existing_user", "password123");

        UserRegistrationDto duplicateUserDto = new UserRegistrationDto();
        duplicateUserDto.setUsername("existing_user");
        duplicateUserDto.setPassword("another_password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUserDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPost_whenNotAuthenticated_shouldReturnForbidden() throws Exception {
        PostCreationDto postDto = new PostCreationDto();
        postDto.setText("This post should fail.");
        MockMultipartFile postPart = new MockMultipartFile("postData", "", "application/json", objectMapper.writeValueAsString(postDto).getBytes());

        mockMvc.perform(multipart("/api/posts").file(postPart))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePost_whenUserIsNotAuthor_shouldReturnForbidden() throws Exception {
        String tokenA = registerAndLogin("author_user", "passwordA");
        String tokenB = registerAndLogin("other_user", "passwordB");
        long postId = createPostAsUser("A post by author_user", tokenA);

        mockMvc.perform(delete("/api/posts/{postId}", postId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden());
    }

    private String registerAndLogin(String username, String password) throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername(username);
        registrationDto.setPassword(password);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated());
        return loginAndGetToken(username, password);
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

    private long createPostAsUser(String text, String token) throws Exception {
        PostCreationDto postDto = new PostCreationDto();
        postDto.setText(text);
        MockMultipartFile postPart = new MockMultipartFile("postData", "", "application/json", objectMapper.writeValueAsString(postDto).getBytes());
        MvcResult result = mockMvc.perform(multipart("/api/posts")
                        .file(postPart)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andReturn();
        String responseString = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseString).get("id").asLong();
    }
    @Test
    void likePost_whenPostDoesNotExist_shouldReturnBadRequest() throws Exception {

        String tokenA = registerAndLogin("userToLike", "password123");
        long nonExistentPostId = 999L;


        mockMvc.perform(post("/api/posts/{postId}/like", nonExistentPostId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isBadRequest());
    }
    @Test
    void followUser_whenFollowingSelf_shouldReturnBadRequest() throws Exception {

        String tokenA = registerAndLogin("userA", "passwordA");


        mockMvc.perform(post("/api/users/{username}/follow", "userA")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("You cannot follow yourself."));
    }
    @Test
    void likePost_whenLikingPostTwice_shouldReturnBadRequest() throws Exception {

        String tokenA = registerAndLogin("userA", "passwordA");
        String tokenB = registerAndLogin("userB", "passwordB");
        long postId = createPostAsUser("A post by userB", tokenB);


        mockMvc.perform(post("/api/posts/{postId}/like", postId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());


        mockMvc.perform(post("/api/posts/{postId}/like", postId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("You have already liked this post."));
    }
    @Test
    void unfollowUser_whenNotFollowing_shouldReturnBadRequest() throws Exception {
        String tokenA = registerAndLogin("userA", "passwordA");
        registerAndLogin("userB", "passwordB");
        mockMvc.perform(post("/api/users/{username}/unfollow", "userB")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isBadRequest());
    }


    @Test
    void deletePost_whenPostDoesNotExist_shouldReturnNotFound() throws Exception {
        String tokenA = registerAndLogin("user_who_deletes", "password123");
        long nonExistentPostId = 999L;
        mockMvc.perform(delete("/api/posts/{postId}", nonExistentPostId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isNotFound());
    }

    @Test
    void followUser_whenTargetUserNotFound_shouldReturnBadRequest() throws Exception {
        String tokenA = registerAndLogin("user_who_follows", "password123");
        String nonExistentUsername = "ghost_user";
        mockMvc.perform(post("/api/users/{username}/follow", nonExistentUsername)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isBadRequest());
    }
}