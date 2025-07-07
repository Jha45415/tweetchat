package oth.ics.wtp.tweetchatbackend.DTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private boolean followedByCurrentUser;
    private int followersCount;
    private int followingCount;
}
