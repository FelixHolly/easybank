package at.holly.easybankbackend.dto;

import at.holly.easybankbackend.model.Contact;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Contact entity and ContactDto
 */
@Component
public class ContactMapper {

    /**
     * Convert Contact entity to ContactDto
     *
     * @param contact the contact entity
     * @return the contact DTO
     */
    public ContactDto toDto(Contact contact) {
        if (contact == null) {
            return null;
        }

        return ContactDto.builder()
                .contactId(contact.getContactId())
                .contactName(contact.getContactName())
                .contactEmail(contact.getContactEmail())
                .subject(contact.getSubject())
                .message(contact.getMessage())
                .createDt(contact.getCreateDt())
                .build();
    }

    /**
     * Convert ContactDto to Contact entity
     *
     * @param dto the contact DTO
     * @return the contact entity
     */
    public Contact toEntity(ContactDto dto) {
        if (dto == null) {
            return null;
        }

        Contact contact = new Contact();
        contact.setContactId(dto.getContactId());
        contact.setContactName(dto.getContactName());
        contact.setContactEmail(dto.getContactEmail());
        contact.setSubject(dto.getSubject());
        contact.setMessage(dto.getMessage());
        contact.setCreateDt(dto.getCreateDt());

        return contact;
    }
}
