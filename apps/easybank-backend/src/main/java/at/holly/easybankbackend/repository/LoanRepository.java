package at.holly.easybankbackend.repository;

import at.holly.easybankbackend.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

  Page<Loan> findByUserId(long userId, Pageable pageable);

  /**
   * Get total loan amount (sum of totalLoan) for user
   */
  @Query("SELECT COALESCE(SUM(l.totalLoan), 0) FROM Loan l WHERE l.userId = :userId")
  BigDecimal sumTotalLoanByUserId(@Param("userId") Long userId);

  /**
   * Get total outstanding amount (sum of outstandingAmount) for user
   */
  @Query("SELECT COALESCE(SUM(l.outstandingAmount), 0) FROM Loan l WHERE l.userId = :userId")
  BigDecimal sumOutstandingByUserId(@Param("userId") Long userId);

  /**
   * Get total paid amount (sum of amountPaid) for user
   */
  @Query("SELECT COALESCE(SUM(l.amountPaid), 0) FROM Loan l WHERE l.userId = :userId")
  BigDecimal sumPaidByUserId(@Param("userId") Long userId);

  /**
   * Get count of active loans (outstandingAmount > 0) for user
   */
  @Query("SELECT COUNT(l) FROM Loan l WHERE l.userId = :userId AND l.outstandingAmount > 0")
  Long countActiveByUserId(@Param("userId") Long userId);

  /**
   * Get total loan count for user
   */
  Long countByUserId(Long userId);

}
