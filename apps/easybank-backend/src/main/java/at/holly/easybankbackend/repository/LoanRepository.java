package at.holly.easybankbackend.repository;

import at.holly.easybankbackend.model.Loan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends CrudRepository<Loan, Long> {

  List<Loan> findByCustomerIdOrderByStartDtDesc(long customerId);

}
