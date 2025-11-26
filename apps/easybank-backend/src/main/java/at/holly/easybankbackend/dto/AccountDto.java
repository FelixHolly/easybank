package at.holly.easybankbackend.dto;

import at.holly.easybankbackend.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * Account Data Transfer Object
 * Used for API responses to avoid exposing entity internals
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Long accountNumber;
    private Long userId;
    private AccountType accountType;
    private String branchAddress;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createDt;
}
