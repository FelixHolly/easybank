package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.NoticeDto;
import at.holly.easybankbackend.dto.NoticeMapper;
import at.holly.easybankbackend.model.Notice;
import at.holly.easybankbackend.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Notices Controller
 * Handles system notice/announcement operations
 */
@RestController
@RequiredArgsConstructor
public class NoticesController {

  private final NoticeRepository noticeRepository;
  private final NoticeMapper noticeMapper;

  /**
   * Get all active system notices
   * This endpoint is public - no authentication required
   * Returns only notices that are currently active (current date within begin/end dates)
   *
   * @return list of active notice DTOs
   */
  @GetMapping("/notices")
  public List<NoticeDto> getNotices() {
    List<Notice> activeNotices = noticeRepository.findAllActiveNotices();
    return noticeMapper.toDtoList(activeNotices);
  }

}
