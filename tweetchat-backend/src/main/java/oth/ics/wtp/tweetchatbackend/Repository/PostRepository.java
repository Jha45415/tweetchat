package oth.ics.wtp.tweetchatbackend.Repository;

import oth.ics.wtp.tweetchatbackend.entity.Post;
import oth.ics.wtp.tweetchatbackend.entity.User;
import org.springframework.data.domain.Pageable; // For pagination (at most 20 posts)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {


    List<Post> findByAuthorOrderByTimestampDesc(User author, Pageable pageable);


    List<Post> findByAuthorInOrderByTimestampDesc(List<User> authors, Pageable pageable);


    @Query("SELECT p FROM Post p WHERE p.author IN :authors ORDER BY p.timestamp DESC")
    List<Post> findPostsByAuthors(@Param("authors") List<User> authors, Pageable pageable);


}