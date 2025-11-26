package at.holly.easybankbackend.dto;

import at.holly.easybankbackend.model.AccountTransaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between AccountTransaction entity and AccountTransactionDto
 */
@Component
public class AccountTransactionMapper {

    /**
     * Convert AccountTransaction entity to AccountTransactionDto
     *
     * @param transaction the account transaction entity
     * @return the account transaction DTO
     */
    public AccountTransactionDto toDto(AccountTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        return AccountTransactionDto.builder()
                .transactionId(transaction.getTransactionId())
                .accountNumber(transaction.getAccountNumber())
                .userId(transaction.getUserId())
                .transactionDt(transaction.getTransactionDt())
                .transactionSummary(transaction.getTransactionSummary())
                .transactionType(transaction.getTransactionType())
                .transactionAmt(transaction.getTransactionAmt())
                .closingBalance(transaction.getClosingBalance())
                .createDt(transaction.getCreateDt())
                .build();
    }

    /**
     * Convert list of AccountTransaction entities to list of AccountTransactionDtos
     *
     * @param transactions the list of account transaction entities
     * @return the list of account transaction DTOs
     */
    public List<AccountTransactionDto> toDtoList(List<AccountTransaction> transactions) {
        if (transactions == null) {
            return null;
        }

        return transactions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert AccountTransactionDto to AccountTransaction entity
     *
     * @param dto the account transaction DTO
     * @return the account transaction entity
     */
    public AccountTransaction toEntity(AccountTransactionDto dto) {
        if (dto == null) {
            return null;
        }

        AccountTransaction transaction = new AccountTransaction();
        transaction.setTransactionId(dto.getTransactionId());
        transaction.setAccountNumber(dto.getAccountNumber());
        transaction.setUserId(dto.getUserId());
        transaction.setTransactionDt(dto.getTransactionDt());
        transaction.setTransactionSummary(dto.getTransactionSummary());
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setTransactionAmt(dto.getTransactionAmt());
        transaction.setClosingBalance(dto.getClosingBalance());
        transaction.setCreateDt(dto.getCreateDt());

        return transaction;
    }
}
