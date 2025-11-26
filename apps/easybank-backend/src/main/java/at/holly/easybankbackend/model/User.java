package at.holly.easybankbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

/**
 * User Entity
 * Represents an authenticated user in the system.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private long id;

  private String name;

  private String email;

  @Column(name = "create_dt")
  @JsonIgnore
  private Date createDt;

}
