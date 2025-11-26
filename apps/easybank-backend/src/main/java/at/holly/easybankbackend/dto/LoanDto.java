package at.holly.easybankbackend.dto;

import at.holly.easybankbackend.enums.LoanType;
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

    private LoanType loanType;
    private Integer totalLoan;
    private Integer amountPaid;
    private Integer outstandingAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createDt;
}
