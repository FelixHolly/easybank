package at.holly.easybankbackend.dto;

import at.holly.easybankbackend.enums.CardType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Card Data Transfer Object
 * Used for API responses to avoid exposing entity internals
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {

    private Long cardId;
    private Long userId;
    private String cardNumber;
    private CardType cardType;
    private BigDecimal totalLimit;
    private BigDecimal amountUsed;
    private BigDecimal availableAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createDt;
}
