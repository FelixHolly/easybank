package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.NoticeDto;
import at.holly.easybankbackend.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Notices Controller
 * Handles system notice/announcement HTTP endpoints
 * Delegates business logic to NoticeService
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NoticesController {

  private final NoticeService noticeService;

  /**
   * Get all active system notices (paginated)
   * This endpoint is public - no authentication required
   * Supports query parameters: ?page=0&size=10&sort=noticBegDt,desc
   *
   * @param pageable pagination and sorting parameters (default: page 0, size 20, sorted by noticBegDt desc)
   * @return page of active notice DTOs
   */
  @GetMapping("/notices")
  public Page<NoticeDto> getNotices(
      @PageableDefault(size = 20, sort = "noticBegDt", direction = Sort.Direction.DESC) Pageable pageable) {
    return noticeService.getActiveNotices(pageable);
  }

}
