package oth.ics.wtp.tweetchatbackend.DTO;



import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AuthTokenDto {
    private String accessToken;
    private String tokenType = "Bearer";

    public AuthTokenDto(String accessToken) {
        this.accessToken = accessToken;
    }
}