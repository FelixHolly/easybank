package at.holly.easybankbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for customer data in API responses
 * Excludes sensitive data like password
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

  private Long id;
  private String name;
  private String email;
  private String password;
  private String mobileNumber;
}
