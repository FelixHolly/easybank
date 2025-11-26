package at.holly.easybankbackend.mapper;

import at.holly.easybankbackend.dto.*;

import at.holly.easybankbackend.model.Notice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Notice entity and NoticeDto
 */
@Component
public class NoticeMapper {

    /**
     * Convert Notice entity to NoticeDto
     * Note: createDt and updateDt are intentionally excluded for security
     *
     * @param notice the notice entity
     * @return the notice DTO
     */
    public NoticeDto toDto(Notice notice) {
        if (notice == null) {
            return null;
        }

        return NoticeDto.builder()
                .noticeId(notice.getNoticeId())
                .noticeSummary(notice.getNoticeSummary())
                .noticeDetails(notice.getNoticeDetails())
                .noticBegDt(notice.getNoticBegDt())
                .noticEndDt(notice.getNoticEndDt())
                .build();
    }

    /**
     * Convert list of Notice entities to list of NoticeDtos
     *
     * @param notices the list of notice entities
     * @return the list of notice DTOs
     */
    public List<NoticeDto> toDtoList(List<Notice> notices) {
        if (notices == null) {
            return null;
        }

        return notices.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert NoticeDto to Notice entity
     *
     * @param dto the notice DTO
     * @return the notice entity
     */
    public Notice toEntity(NoticeDto dto) {
        if (dto == null) {
            return null;
        }

        Notice notice = new Notice();
        notice.setNoticeId(dto.getNoticeId());
        notice.setNoticeSummary(dto.getNoticeSummary());
        notice.setNoticeDetails(dto.getNoticeDetails());
        notice.setNoticBegDt(dto.getNoticBegDt());
        notice.setNoticEndDt(dto.getNoticEndDt());

        return notice;
    }
}
