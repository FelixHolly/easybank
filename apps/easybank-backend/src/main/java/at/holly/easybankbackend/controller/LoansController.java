package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.model.Loan;
import at.holly.easybankbackend.repository.CustomerRepository;
import at.holly.easybankbackend.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Loans Controller
 * Handles loan-related operations with authority-based access control
 */
@RestController
@RequiredArgsConstructor
public class LoansController {

  private final LoanRepository loanRepository;
  private final CustomerRepository customerRepository;

  /**
   * Get loan details for a customer
   * Requires LOAN:READ authority
   */
  @GetMapping("/myLoans")
  public List<Loan> getLoansDetails (@RequestParam String email) {
    Optional<Customer> maybeCustomer = customerRepository.findByEmail(email);
    if (maybeCustomer.isEmpty()) {
      throw new RuntimeException("Customer not found");
    }

    return loanRepository.findByCustomerIdOrderByStartDtDesc(maybeCustomer.get().getId());
  }

}
