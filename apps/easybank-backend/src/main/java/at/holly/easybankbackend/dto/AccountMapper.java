package at.holly.easybankbackend.dto;

import at.holly.easybankbackend.model.Account;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Account entity and AccountDto
 */
@Component
public class AccountMapper {

    /**
     * Convert Account entity to AccountDto
     *
     * @param account the account entity
     * @return the account DTO
     */
    public AccountDto toDto(Account account) {
        if (account == null) {
            return null;
        }

        return AccountDto.builder()
                .accountNumber(account.getAccountNumber())
                .userId(account.getUserId())
                .accountType(account.getAccountType())
                .branchAddress(account.getBranchAddress())
                .createDt(account.getCreateDt())
                .build();
    }

    /**
     * Convert AccountDto to Account entity
     *
     * @param dto the account DTO
     * @return the account entity
     */
    public Account toEntity(AccountDto dto) {
        if (dto == null) {
            return null;
        }

        Account account = new Account();
        account.setAccountNumber(dto.getAccountNumber());
        account.setUserId(dto.getUserId());
        account.setAccountType(dto.getAccountType());
        account.setBranchAddress(dto.getBranchAddress());
        account.setCreateDt(dto.getCreateDt());

        return account;
    }
}
