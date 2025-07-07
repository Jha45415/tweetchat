package oth.ics.wtp.tweetchatbackend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    private Post post;
    private User author;
    private User liker;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setUsername("author");

        liker = new User();
        liker.setId(2L);
        liker.setUsername("liker");

        post = new Post();
        post.setId(100L);
        post.setText("A test post");
        post.setTimestamp(LocalDateTime.now());
        post.setAuthor(author);
    }

    @Test
    void testPostCreationAndGetters() {
        assertNotNull(post);
        assertEquals(100L, post.getId());
        assertEquals("A test post", post.getText());
        assertEquals(author, post.getAuthor());
        assertNotNull(post.getTimestamp());
    }

    @Test
    void getLikesCount_whenNoLikes_shouldReturnZero() {
        assertEquals(0, post.getLikesCount());
    }

    @Test
    void addLike_shouldIncreaseLikesCount() {
        post.addLike(liker);
        assertEquals(1, post.getLikesCount());
        assertTrue(post.getLikedByUsers().contains(liker));
    }

    @Test
    void addLike_whenUserAlreadyLiked_shouldNotIncreaseCount() {
        post.addLike(liker);
        post.addLike(liker);
        assertEquals(1, post.getLikesCount());
    }

    @Test
    void removeLike_shouldDecreaseLikesCount() {
        post.addLike(liker);
        assertEquals(1, post.getLikesCount());

        post.removeLike(liker);
        assertEquals(0, post.getLikesCount());
        assertFalse(post.getLikedByUsers().contains(liker));
    }

    @Test
    void removeLike_whenUserHasNotLiked_shouldDoNothing() {
        post.removeLike(liker);
        assertEquals(0, post.getLikesCount());
    }

    @Test
    void testConstructorWithTextAndAuthor() {
        Post newPost = new Post("A new post", author);
        assertEquals("A new post", newPost.getText());
        assertEquals(author, newPost.getAuthor());
    }

    @Test
    void testEqualsAndHashCode() {
        Post samePost = new Post();
        samePost.setId(100L);

        Post differentPost = new Post();
        differentPost.setId(101L);

        Post nullIdPost = new Post();

        assertEquals(post, post);
        assertEquals(post, samePost);
        assertEquals(post.hashCode(), samePost.hashCode());
        assertNotEquals(post, differentPost);
        assertNotEquals(post, null);
        assertNotEquals(post, new Object());
        assertNotEquals(nullIdPost, post);
    }

    @Test
    void testToString() {
        String postString = post.toString();
        assertTrue(postString.contains("id=100"));
        assertTrue(postString.contains("text='A test post'"));
        assertTrue(postString.contains("authorUsername=author"));
        assertTrue(postString.contains("likesCount=0"));
    }
}