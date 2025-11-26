package at.holly.easybankbackend.repository;

import at.holly.easybankbackend.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 * Provides CRUD operations for user data
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  /**
   * Find user by email address
   * @param email the user's email
   * @return Optional containing the user if found
   */
  Optional<User> findByEmail(String email);

}
