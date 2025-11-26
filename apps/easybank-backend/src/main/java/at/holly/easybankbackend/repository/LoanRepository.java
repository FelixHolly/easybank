package at.holly.easybankbackend.repository;

import at.holly.easybankbackend.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

  Page<Loan> findByUserId(long userId, Pageable pageable);

}
