package at.holly.easybankbackend.mapper;

import at.holly.easybankbackend.dto.*;

import at.holly.easybankbackend.model.Card;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Card entity and CardDto
 */
@Component
public class CardMapper {

    /**
     * Convert Card entity to CardDto
     *
     * @param card the card entity
     * @return the card DTO
     */
    public CardDto toDto(Card card) {
        if (card == null) {
            return null;
        }

        return CardDto.builder()
                .cardId(card.getCardId())
                .userId(card.getUserId())
                .cardNumber(card.getCardNumber())
                .cardType(card.getCardType())
                .totalLimit(card.getTotalLimit())
                .amountUsed(card.getAmountUsed())
                .availableAmount(card.getAvailableAmount())
                .createDt(card.getCreateDt())
                .build();
    }

    /**
     * Convert list of Card entities to list of CardDtos
     *
     * @param cards the list of card entities
     * @return the list of card DTOs
     */
    public List<CardDto> toDtoList(List<Card> cards) {
        if (cards == null) {
            return null;
        }

        return cards.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert CardDto to Card entity
     *
     * @param dto the card DTO
     * @return the card entity
     */
    public Card toEntity(CardDto dto) {
        if (dto == null) {
            return null;
        }

        Card card = new Card();
        card.setCardId(dto.getCardId());
        card.setUserId(dto.getUserId());
        card.setCardNumber(dto.getCardNumber());
        card.setCardType(dto.getCardType());
        card.setTotalLimit(dto.getTotalLimit());
        card.setAmountUsed(dto.getAmountUsed());
        card.setAvailableAmount(dto.getAvailableAmount());
        card.setCreateDt(dto.getCreateDt());

        return card;
    }
}
