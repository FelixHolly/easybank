package at.holly.easybankbackend.repository;

import at.holly.easybankbackend.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

  Page<Card> findByUserId(long userId, Pageable pageable);

  /**
   * Get total credit limit (sum of totalLimit) for user
   */
  @Query("SELECT COALESCE(SUM(c.totalLimit), 0) FROM Card c WHERE c.userId = :userId")
  BigDecimal sumTotalLimitByUserId(@Param("userId") Long userId);

  /**
   * Get total available amount (sum of availableAmount) for user
   */
  @Query("SELECT COALESCE(SUM(c.availableAmount), 0) FROM Card c WHERE c.userId = :userId")
  BigDecimal sumAvailableByUserId(@Param("userId") Long userId);

  /**
   * Get total used amount (sum of amountUsed) for user
   */
  @Query("SELECT COALESCE(SUM(c.amountUsed), 0) FROM Card c WHERE c.userId = :userId")
  BigDecimal sumUsedByUserId(@Param("userId") Long userId);

  /**
   * Get total card count for user
   */
  Long countByUserId(Long userId);

}
