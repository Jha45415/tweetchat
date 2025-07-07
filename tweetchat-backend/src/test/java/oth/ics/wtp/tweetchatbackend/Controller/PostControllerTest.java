package oth.ics.wtp.tweetchatbackend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import oth.ics.wtp.tweetchatbackend.DTO.PostCreationDto;
import oth.ics.wtp.tweetchatbackend.DTO.UserLoginDto;
import oth.ics.wtp.tweetchatbackend.Service.UserService;
import oth.ics.wtp.tweetchatbackend.entity.Post;
import oth.ics.wtp.tweetchatbackend.entity.User;
import oth.ics.wtp.tweetchatbackend.Repository.PostRepository;
import oth.ics.wtp.tweetchatbackend.Repository.UserRepository;
import oth.ics.wtp.tweetchatbackend.Service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User userA;
    private User userB;
    private String tokenA;
    private String tokenB;

    @BeforeEach
    void setUp() throws Exception {
        postRepository.deleteAll();
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
        tokenB = loginAndGetToken("userB", "passwordB");
    }

    @Test
    void createPost_whenAuthenticated_shouldSucceed() throws Exception {
        PostCreationDto postDto = new PostCreationDto();
        postDto.setText("A new post from userA");

        MockMultipartFile postPart = new MockMultipartFile(
                "postData", "", "application/json", objectMapper.writeValueAsString(postDto).getBytes()
        );

        mockMvc.perform(multipart("/api/posts")
                        .file(postPart)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text", is("A new post from userA")))
                .andExpect(jsonPath("$.authorUsername", is("userA")));
    }

    @Test
    void getTimelineFeed_whenFollowingUser_shouldReturnTheirPosts() throws Exception {
        userService.followUser("userA", "userB");
        postService.createPost(new PostCreationDto("Post by B"), "userB", null);

        mockMvc.perform(get("/api/posts/feed")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text", is("Post by B")));
    }

    @Test
    void deletePost_whenUserIsAuthor_shouldReturnOk() throws Exception {
        Post post = postService.createPost(new PostCreationDto("Post to be deleted"), "userA", null);
        assertEquals(1, postRepository.count());

        mockMvc.perform(delete("/api/posts/{postId}", post.getId())
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Post deleted successfully."));

        assertEquals(0, postRepository.count());
    }

    @Test
    void deletePost_whenUserIsNotAuthor_shouldReturnForbidden() throws Exception {
        Post post = postService.createPost(new PostCreationDto("A post by userA"), "userA", null);
        assertEquals(1, postRepository.count());

        mockMvc.perform(delete("/api/posts/{postId}", post.getId())
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").value("User is not authorized to delete this post."));

        assertEquals(1, postRepository.count());
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
    void likePost_whenPostNotFound_shouldReturnBadRequest() throws Exception {

        long nonExistentPostId = 999L;


        mockMvc.perform(post("/api/posts/{postId}/like", nonExistentPostId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Post not found with ID: " + nonExistentPostId));
    }
    @Test
    void likePost_whenPostDoesNotExist_shouldReturnBadRequest() throws Exception {
        long nonExistentPostId = 999L;

        mockMvc.perform(post("/api/posts/{postId}/like", nonExistentPostId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isBadRequest());
    }
    @Test
    void deletePost_whenPostNotFound_shouldReturnNotFound() throws Exception {
        long nonExistentPostId = 999L;

        mockMvc.perform(delete("/api/posts/{postId}", nonExistentPostId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isNotFound());
    }
}