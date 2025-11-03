package at.holly.easybankbackend.repository;

import at.holly.easybankbackend.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

  Account findByCustomerId(long customerId);

}
