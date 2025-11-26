package at.holly.easybankbackend.dto;

import at.holly.easybankbackend.model.Loan;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Loan entity and LoanDto
 */
@Component
public class LoanMapper {

    /**
     * Convert Loan entity to LoanDto
     *
     * @param loan the loan entity
     * @return the loan DTO
     */
    public LoanDto toDto(Loan loan) {
        if (loan == null) {
            return null;
        }

        return LoanDto.builder()
                .loanNumber(loan.getLoanNumber())
                .userId(loan.getUserId())
                .startDt(loan.getStartDt())
                .loanType(loan.getLoanType())
                .totalLoan(loan.getTotalLoan())
                .amountPaid(loan.getAmountPaid())
                .outstandingAmount(loan.getOutstandingAmount())
                .createDt(loan.getCreateDt())
                .build();
    }

    /**
     * Convert list of Loan entities to list of LoanDtos
     *
     * @param loans the list of loan entities
     * @return the list of loan DTOs
     */
    public List<LoanDto> toDtoList(List<Loan> loans) {
        if (loans == null) {
            return null;
        }

        return loans.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert LoanDto to Loan entity
     *
     * @param dto the loan DTO
     * @return the loan entity
     */
    public Loan toEntity(LoanDto dto) {
        if (dto == null) {
            return null;
        }

        Loan loan = new Loan();
        loan.setLoanNumber(dto.getLoanNumber());
        loan.setUserId(dto.getUserId());
        loan.setStartDt(dto.getStartDt());
        loan.setLoanType(dto.getLoanType());
        loan.setTotalLoan(dto.getTotalLoan());
        loan.setAmountPaid(dto.getAmountPaid());
        loan.setOutstandingAmount(dto.getOutstandingAmount());
        loan.setCreateDt(dto.getCreateDt());

        return loan;
    }
}
