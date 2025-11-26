package at.holly.easybankbackend.repository;

import at.holly.easybankbackend.model.AccountTransaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountTransactionRepository extends CrudRepository<AccountTransaction, String> {

  List<AccountTransaction> findByUserIdOrderByTransactionDtDesc(long userId);

}
