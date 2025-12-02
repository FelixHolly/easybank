package at.holly.easybankbackend.repository;

import at.holly.easybankbackend.model.AccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, String> {

  Page<AccountTransaction> findByUserId(long userId, Pageable pageable);

  /**
   * Get most recent transaction for user (for current balance)
   */
  @Query("SELECT t FROM AccountTransaction t WHERE t.userId = :userId ORDER BY t.transactionDt DESC LIMIT 1")
  Optional<AccountTransaction> findLatestByUserId(@Param("userId") Long userId);

  /**
   * Get total credits (money received) for user
   */
  @Query("SELECT COALESCE(SUM(t.transactionAmt), 0) FROM AccountTransaction t WHERE t.userId = :userId AND t.transactionType = 'Credit'")
  BigDecimal sumCreditsByUserId(@Param("userId") Long userId);

  /**
   * Get total debits (money spent) for user
   */
  @Query("SELECT COALESCE(SUM(t.transactionAmt), 0) FROM AccountTransaction t WHERE t.userId = :userId AND t.transactionType = 'Debit'")
  BigDecimal sumDebitsByUserId(@Param("userId") Long userId);

  /**
   * Get total transaction count for user
   */
  Long countByUserId(Long userId);

}
