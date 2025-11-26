package at.holly.easybankbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * Loan Data Transfer Object
 * Used for API responses to avoid exposing entity internals
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {

    private Long loanNumber;
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDt;

    private String loanType;
    private Integer totalLoan;
    private Integer amountPaid;
    private Integer outstandingAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createDt;
}
