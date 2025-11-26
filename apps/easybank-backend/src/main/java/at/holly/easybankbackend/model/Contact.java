package at.holly.easybankbackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.sql.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contact_messages")
public class Contact {

  @Id
  @Column(name = "contact_id")
  private String contactId;

  @Column(name = "contact_name")
  private String contactName;

  @Column(name = "contact_email")
  private String contactEmail;

  private String subject;

  private String message;

  @Column(name = "create_dt")
  private Date createDt;

}
