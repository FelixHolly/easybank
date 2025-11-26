package at.holly.easybankbackend.dto;

import at.holly.easybankbackend.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between User entity and DTOs
 */
@Component
public class UserMapper {

  /**
   * Convert User entity to UserDto
   */
  public UserDto toDto(User user) {
    return UserDto.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .build();
  }
}
