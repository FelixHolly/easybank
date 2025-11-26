package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.NoticeDto;
import at.holly.easybankbackend.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Notices Controller
 * Handles system notice/announcement HTTP endpoints
 * Delegates business logic to NoticeService
 */
@RestController
@RequiredArgsConstructor
public class NoticesController {

  private final NoticeService noticeService;

  /**
   * Get all active system notices
   * This endpoint is public - no authentication required
   *
   * @return list of active notice DTOs
   */
  @GetMapping("/notices")
  public List<NoticeDto> getNotices() {
    return noticeService.getActiveNotices();
  }

}
