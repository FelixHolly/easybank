package at.holly.easybankbackend.dto;

import at.holly.easybankbackend.model.Customer;
import org.springframework.stereotype.Component;

import java.sql.Date;

/**
 * Mapper to convert between Customer entity and DTOs
 */
@Component
public class CustomerMapper {

  /**
   * Convert RegisterRequestDto to Customer entity
   */
  public Customer toEntity(CustomerDto dto) {
    Customer customer = new Customer();
    customer.setName(dto.getName());
    customer.setEmail(dto.getEmail());
    customer.setMobileNumber(dto.getMobileNumber());
    customer.setPassword(dto.getPassword()); // Will be hashed in controller
    customer.setCreateDt(new Date(System.currentTimeMillis()));
    return customer;
  }

  /**
   * Convert Customer entity to CustomerResponseDto
   */
  public CustomerDto toDto(Customer customer) {
    return CustomerDto.builder()
        .id(customer.getId())
        .name(customer.getName())
        .email(customer.getEmail())
        .mobileNumber(customer.getMobileNumber())
        .build();
  }
}
