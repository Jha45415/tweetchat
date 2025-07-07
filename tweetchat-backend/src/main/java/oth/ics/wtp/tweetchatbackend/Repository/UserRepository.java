package oth.ics.wtp.tweetchatbackend.Repository;
import oth.ics.wtp.tweetchatbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByUsername(String username);


    List<User> findByUsernameContainingIgnoreCase(String searchTerm);


    boolean existsByUsername(String username);


}