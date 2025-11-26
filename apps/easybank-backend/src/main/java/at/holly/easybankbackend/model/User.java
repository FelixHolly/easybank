package at.holly.easybankbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

/**
 * User Entity
 * Represents an authenticated user in the system.
 */
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true)
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private long id;

  private String name;

  @Column(unique = true)
  private String email;

  @Column(name = "create_dt")
  @JsonIgnore
  private Date createDt;

}
