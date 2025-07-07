package oth.ics.wtp.tweetchatbackend.Service;


import oth.ics.wtp.tweetchatbackend.DTO.PostCreationDto;
import oth.ics.wtp.tweetchatbackend.entity.Post;
import oth.ics.wtp.tweetchatbackend.entity.User;
import oth.ics.wtp.tweetchatbackend.Repository.PostRepository;
import oth.ics.wtp.tweetchatbackend.Repository.UserRepository;
import oth.ics.wtp.tweetchatbackend.DTO.PostDto; // Make sure to import your new DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import oth.ics.wtp.tweetchatbackend.entity.Post;
import oth.ics.wtp.tweetchatbackend.entity.User;
import org.springframework.web.multipart.MultipartFile;


@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, FileStorageService fileStorageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public Post createPost(PostCreationDto postDto, String authorUsername,MultipartFile imageFile) {

        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new IllegalArgumentException("Author with username " + authorUsername + " not found."));


        Post newPost = new Post();
        newPost.setText(postDto.getText());
        newPost.setAuthor(author);
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(imageFile);

            String imageUrl = "/uploads/" + fileName;
            newPost.setImageUrl(imageUrl);
        }

        return postRepository.save(newPost);
    }

    /**
     * Converts a Post entity to a PostDto.
     * This is a crucial step to prevent lazy loading exceptions and to control the data sent to the client.
     * @param post The Post entity to convert.
     * @return A PostDto object.
     */


    public PostDto mapPostToDto(Post post, User currentUser) {
        if (post == null) return null;

        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setText(post.getText());
        dto.setTimestamp(post.getTimestamp());
        dto.setImageUrl(post.getImageUrl());

        if (post.getAuthor() != null) {
            dto.setAuthorUsername(post.getAuthor().getUsername());
        }

        dto.setLikesCount(post.getLikesCount());


        if (currentUser != null) {
            dto.setLikedByCurrentUser(post.getLikedByUsers().contains(currentUser));
        } else {
            dto.setLikedByCurrentUser(false);
        }

        return dto;
    }
    /**
     * Fetches the aggregated timeline for a given user.
     * This includes posts from all users that the specified user is following.
     * @param username The username of the user whose feed we want to fetch.
     * @return A list of PostDto objects.
     */

    @Transactional(readOnly = true)
    public List<PostDto> getAggregatedTimeline(String username, int page) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        List<User> followedUsers = List.copyOf(currentUser.getFollowing());

        if (followedUsers.isEmpty()) {
            return Collections.emptyList();
        }

        Pageable pageable = PageRequest.of(page, 20);
        List<Post> timelinePosts = postRepository.findByAuthorInOrderByTimestampDesc(followedUsers, pageable);

        return timelinePosts.stream()
                .map(post -> mapPostToDto(post, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional
    public void likePost(Long postId, String username) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));


        if (post.getAuthor().equals(user)) {
            throw new IllegalArgumentException("You cannot like your own post.");
        }

        if (post.getLikedByUsers().contains(user)) {

            throw new IllegalArgumentException("You have already liked this post.");
        }


        post.addLike(user);

    }


    @Transactional
    public void unlikePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        if (!post.getLikedByUsers().contains(user)) {
            throw new IllegalArgumentException("You have not liked this post.");
        }

        post.removeLike(user);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPostsByUsername(String username, String currentUsername, int page) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));


        final User currentUser = (currentUsername != null)
                ? userRepository.findByUsername(currentUsername).orElse(null)
                : null;


        Pageable pageable = PageRequest.of(page, 20);
        List<Post> posts = postRepository.findByAuthorOrderByTimestampDesc(user, pageable);


        return posts.stream()
                .map(post -> mapPostToDto(post, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePost(Long postId, String currentUsername) {

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));


        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));


        if (!post.getAuthor().equals(currentUser)) {

            throw new IllegalStateException("User is not authorized to delete this post.");
        }


        postRepository.delete(post);
    }
}