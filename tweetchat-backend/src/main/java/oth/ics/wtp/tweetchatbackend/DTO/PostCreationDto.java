package oth.ics.wtp.tweetchatbackend.DTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreationDto {

    @NotBlank(message = "Post text cannot be blank")
    @Size(max = 280, message = "Post text cannot exceed 200 characters")
    private String text;
}