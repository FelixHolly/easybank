package at.holly.easybankbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "customer_id")
  private long id;

  private String name;

  private String email;

  @Column(name = "mobile_number")
  private String mobileNumber;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  /**
   * Customer roles stored in a separate table (customer_roles).
   * Using @ElementCollection creates a one-to-many relationship.
   * Using Set prevents duplicate roles.
   * Using @Enumerated(STRING) stores the enum name as string for readability.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "customer_roles", joinColumns = @JoinColumn(name = "customer_id"))
  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private Set<Role> roles = new HashSet<>();

  /**
   * Customer authorities (fine-grained permissions) stored in a separate table (customer_authorities).
   * Authorities provide specific operation-level permissions (e.g., ACCOUNT:READ, TRANSACTION:WRITE).
   * Roles provide high-level access, authorities provide specific permissions.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "customer_authorities", joinColumns = @JoinColumn(name = "customer_id"))
  @Column(name = "authority")
  @Enumerated(EnumType.STRING)
  private Set<Authority> authorities = new HashSet<>();

  @Column(name = "create_dt")
  @JsonIgnore
  private Date createDt;

}
