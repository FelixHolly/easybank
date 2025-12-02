package at.holly.easybankbackend.dto;

import at.holly.easybankbackend.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Account Transaction Data Transfer Object
 * Used for API responses to avoid exposing entity internals
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransactionDto {

    private String transactionId;
    private Long accountNumber;
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date transactionDt;

    private String transactionSummary;
    private TransactionType transactionType;
    private BigDecimal transactionAmt;
    private BigDecimal closingBalance;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createDt;
}
