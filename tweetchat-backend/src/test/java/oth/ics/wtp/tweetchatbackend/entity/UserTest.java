package oth.ics.wtp.tweetchatbackend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
    }

    @Test
    void followUser_shouldUpdateFollowingAndFollowersSets() {
        user1.followUser(user2);
        assertTrue(user1.getFollowing().contains(user2));
        assertTrue(user2.getFollowers().contains(user1));
    }

    @Test
    void unfollowUser_shouldUpdateFollowingAndFollowersSets() {
        user1.followUser(user2);
        user1.unfollowUser(user2);
        assertFalse(user1.getFollowing().contains(user2));
        assertFalse(user2.getFollowers().contains(user1));
    }

    @Test
    void addPost_shouldSetAuthorOnPost() {
        Post post = new Post();
        user1.addPost(post);
        assertTrue(user1.getPosts().contains(post));
        assertEquals(user1, post.getAuthor());
    }

    @Test
    void removePost_shouldUnsetAuthorOnPost() {
        Post post = new Post();
        user1.addPost(post);
        user1.removePost(post);
        assertFalse(user1.getPosts().contains(post));
        assertNull(post.getAuthor());
    }

    @Test
    void testEqualsAndHashCode() {
        User sameAsUser1 = new User();
        sameAsUser1.setId(1L);
        sameAsUser1.setUsername("user1");

        User userWithDifferentId = new User();
        userWithDifferentId.setId(3L);
        userWithDifferentId.setUsername("user1");

        assertEquals(user1, sameAsUser1);
        assertEquals(user1.hashCode(), sameAsUser1.hashCode());
        assertNotEquals(user1, user2);
        assertNotEquals(user1, userWithDifferentId);
    }
    @Test
    void testUserConstructorAndSetters() {

        User user = new User("constructorUser", "password");

        assertEquals("constructorUser", user.getUsername());
        assertEquals("password", user.getPassword());

        user.setId(5L);
        user.setUsername("newUser");
        user.setPassword("newPassword");

        assertEquals(5L, user.getId());
        assertEquals("newUser", user.getUsername());
        assertEquals("newPassword", user.getPassword());
    }

    @Test
    void followAndUnfollowUser_shouldUpdateFollowingAndFollowersSets() {
        assertEquals(0, user1.getFollowing().size());
        assertEquals(0, user2.getFollowers().size());

        user1.followUser(user2);

        assertEquals(1, user1.getFollowing().size());
        assertTrue(user1.getFollowing().contains(user2));
        assertEquals(1, user2.getFollowers().size());
        assertTrue(user2.getFollowers().contains(user1));

        user1.unfollowUser(user2);

        assertEquals(0, user1.getFollowing().size());
        assertEquals(0, user2.getFollowers().size());
    }

    @Test
    void addAndRemovePost_shouldModifyPostsList() {
        Post post = new Post();
        assertEquals(0, user1.getPosts().size());

        user1.addPost(post);
        assertEquals(1, user1.getPosts().size());
        assertEquals(user1, post.getAuthor());

        user1.removePost(post);
        assertEquals(0, user1.getPosts().size());
        assertNull(post.getAuthor());
    }
}