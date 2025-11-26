package at.holly.easybankbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user data in API responses
 * Excludes sensitive data like password
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  private Long id;

  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
  private String password;

  @Pattern(regexp = "^[0-9]{10,15}$", message = "Mobile number must be 10-15 digits")
  private String mobileNumber;
}
