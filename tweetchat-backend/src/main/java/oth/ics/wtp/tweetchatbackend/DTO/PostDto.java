package oth.ics.wtp.tweetchatbackend.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String text;
    private LocalDateTime timestamp;
    private String authorUsername;
    private int likesCount;
    private boolean likedByCurrentUser;
    private String imageUrl;
}