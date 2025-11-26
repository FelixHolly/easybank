package at.holly.easybankbackend.repository;

import at.holly.easybankbackend.model.AccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, String> {

  Page<AccountTransaction> findByUserId(long userId, Pageable pageable);

}
