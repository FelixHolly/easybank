package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.UserDto;
import at.holly.easybankbackend.mapper.UserMapper;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.service.UserProvisioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User Controller
 * Handles user profile operations with automatic user provisioning
 * Uses DTOs to avoid exposing entity details
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserProvisioningService userProvisioningService;
  private final UserMapper userMapper;

  /**
   * Get current user details
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   * Returns DTO without sensitive information like password
   */
  @GetMapping("/user")
  public UserDto getUserDetails(Authentication authentication) {
    log.info("GET /user - Fetching user details");

    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);

    log.info("User details retrieved (ID: {})", user.getId());
    return userMapper.toDto(user);
  }
}
