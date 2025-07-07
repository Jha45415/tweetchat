package oth.ics.wtp.tweetchatbackend.Service;

import oth.ics.wtp.tweetchatbackend.DTO.PostCreationDto;
import oth.ics.wtp.tweetchatbackend.entity.Post;
import oth.ics.wtp.tweetchatbackend.entity.User;
import oth.ics.wtp.tweetchatbackend.Repository.PostRepository;
import oth.ics.wtp.tweetchatbackend.Repository.UserRepository;
import oth.ics.wtp.tweetchatbackend.DTO.PostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    private User userA;
    private User userB;
    private Post post;

    @BeforeEach
    void setUp() {
        userA = new User();
        userA.setId(1L);
        userA.setUsername("userA");

        userB = new User();
        userB.setId(2L);
        userB.setUsername("userB");

        post = new Post();
        post.setId(100L);
        post.setText("A post by userA");
        post.setAuthor(userA);
    }

    @Test
    void createPost_whenUserExists_shouldCreateAndSavePost() {
        PostCreationDto postDto = new PostCreationDto();
        postDto.setText("New test post");
        when(userRepository.findByUsername("userA")).thenReturn(Optional.of(userA));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Post createdPost = postService.createPost(postDto, "userA", null);
        assertNotNull(createdPost);
        assertEquals("New test post", createdPost.getText());
        assertEquals("userA", createdPost.getAuthor().getUsername());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void likePost_whenUserLikesAnotherUsersPost_shouldAddLike() {
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("userB")).thenReturn(Optional.of(userB));
        postService.likePost(100L, "userB");
        assertTrue(post.getLikedByUsers().contains(userB));
    }

    @Test
    void getAggregatedTimeline_whenUserFollowsOthers_shouldReturnTheirPosts() {
        User userC = new User();
        userC.setUsername("userC");
        Post postFromC = new Post();
        postFromC.setAuthor(userC);
        userA.getFollowing().add(userC);
        when(userRepository.findByUsername("userA")).thenReturn(Optional.of(userA));
        when(postRepository.findByAuthorInOrderByTimestampDesc(anyList(), any(Pageable.class)))
                .thenReturn(Collections.singletonList(postFromC));
        List<PostDto> timeline = postService.getAggregatedTimeline("userA", 0);
        assertFalse(timeline.isEmpty());
        assertEquals(1, timeline.size());
        assertEquals("userC", timeline.get(0).getAuthorUsername());
    }

    @Test
    void unlikePost_whenUserHasLikedPost_shouldRemoveLike() {
        post.getLikedByUsers().add(userB);
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("userB")).thenReturn(Optional.of(userB));
        postService.unlikePost(100L, "userB");
        assertFalse(post.getLikedByUsers().contains(userB));
    }

    @Test
    void likePost_whenPostNotFound_shouldThrowException() {

        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {

            postService.likePost(99L, "any_user");
        });
    }
    @Test
    void deletePost_whenPostNotFound_shouldThrowException() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());
        when(userRepository.findByUsername("userA")).thenReturn(Optional.of(userA));
        assertThrows(IllegalArgumentException.class, () -> {
            postService.deletePost(99L, "userA");
        });
    }

    @Test
    void deletePost_whenUserIsNotAuthor_shouldThrowException() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("userB")).thenReturn(Optional.of(userB));
        assertThrows(IllegalStateException.class, () -> {
            postService.deletePost(post.getId(), "userB");
        });
    }
}