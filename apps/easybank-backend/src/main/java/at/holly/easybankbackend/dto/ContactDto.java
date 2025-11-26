package at.holly.easybankbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * Contact Data Transfer Object
 * Used for API requests and responses to avoid exposing entity internals
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {

    private String contactId;
    private String contactName;
    private String contactEmail;
    private String subject;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createDt;
}
