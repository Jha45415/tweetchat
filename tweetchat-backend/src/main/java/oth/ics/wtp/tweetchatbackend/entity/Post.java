package oth.ics.wtp.tweetchatbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Post text cannot be blank")
    @Size(max = 280, message = "Post text cannot exceed 280 characters")
    @Column(nullable = false, length = 280)
    private String text;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedByUsers = new HashSet<>();


    public Post(String text, User author) {
        this.text = text;
        this.author = author;

    }


    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }


    public int getLikesCount() {
        return likedByUsers.size();
    }

    public void addLike(User user) {
        this.likedByUsers.add(user);

    }

    public void removeLike(User user) {
        this.likedByUsers.remove(user);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                ", authorUsername=" + (author != null ? author.getUsername() : "null") +
                ", likesCount=" + getLikesCount() +
                '}';
    }

    @Column(nullable = true)
    private String imageUrl;
}