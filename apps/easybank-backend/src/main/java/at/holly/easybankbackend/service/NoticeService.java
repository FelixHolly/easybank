package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.NoticeDto;
import at.holly.easybankbackend.dto.NoticeMapper;
import at.holly.easybankbackend.model.Notice;
import at.holly.easybankbackend.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Notice Service
 * Handles business logic for system notice/announcement operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {

  private final NoticeRepository noticeRepository;
  private final NoticeMapper noticeMapper;

  /**
   * Get all active system notices
   * Returns only notices that are currently active (current date within begin/end dates)
   *
   * @return list of active notice DTOs
   */
  @Transactional(readOnly = true)
  public List<NoticeDto> getActiveNotices() {
    log.info("Fetching active system notices");

    List<Notice> activeNotices = noticeRepository.findAllActiveNotices();
    log.info("Retrieved {} active notices", activeNotices.size());

    return noticeMapper.toDtoList(activeNotices);
  }
}
