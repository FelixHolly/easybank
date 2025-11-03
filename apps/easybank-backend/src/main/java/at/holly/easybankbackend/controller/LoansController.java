package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Loan;
import at.holly.easybankbackend.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoansController {

  private final LoanRepository loanRepository;

  @GetMapping("/myLoans")
  public List<Loan> getLoansDetails (@RequestParam long id) {
    return loanRepository.findByCustomerIdOrderByStartDtDesc(id);
  }

}
