package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.NoticeDto;
import at.holly.easybankbackend.mapper.NoticeMapper;
import at.holly.easybankbackend.model.Notice;
import at.holly.easybankbackend.repository.NoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NoticeService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NoticeService Tests")
class NoticeServiceTest {

  @Mock
  private NoticeRepository noticeRepository;

  @Mock
  private NoticeMapper noticeMapper;

  @InjectMocks
  private NoticeService noticeService;

  @Test
  @DisplayName("Should get all active notices successfully")
  void shouldGetAllActiveNotices() {
    // Given
    Notice notice1 = new Notice();
    notice1.setNoticeId(1L);
    notice1.setNoticeSummary("System Maintenance");
    notice1.setNoticeDetails("Scheduled maintenance on Sunday");
    notice1.setNoticBegDt(new Date(System.currentTimeMillis()));
    notice1.setNoticEndDt(new Date(System.currentTimeMillis() + 86400000));

    Notice notice2 = new Notice();
    notice2.setNoticeId(2L);
    notice2.setNoticeSummary("New Feature");
    notice2.setNoticeDetails("Check out our new dashboard");
    notice2.setNoticBegDt(new Date(System.currentTimeMillis()));
    notice2.setNoticEndDt(new Date(System.currentTimeMillis() + 86400000));

    List<Notice> notices = Arrays.asList(notice1, notice2);

    NoticeDto dto1 = NoticeDto.builder()
        .noticeId(1L)
        .noticeSummary("System Maintenance")
        .noticeDetails("Scheduled maintenance on Sunday")
        .build();

    NoticeDto dto2 = NoticeDto.builder()
        .noticeId(2L)
        .noticeSummary("New Feature")
        .noticeDetails("Check out our new dashboard")
        .build();

    List<NoticeDto> dtos = Arrays.asList(dto1, dto2);

    when(noticeRepository.findAllActiveNotices()).thenReturn(notices);
    when(noticeMapper.toDtoList(notices)).thenReturn(dtos);

    // When
    List<NoticeDto> result = noticeService.getActiveNotices();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getNoticeSummary()).isEqualTo("System Maintenance");
    assertThat(result.get(1).getNoticeSummary()).isEqualTo("New Feature");

    verify(noticeRepository).findAllActiveNotices();
    verify(noticeMapper).toDtoList(notices);
  }

  @Test
  @DisplayName("Should return empty list when no active notices exist")
  void shouldReturnEmptyListWhenNoActiveNotices() {
    // Given
    when(noticeRepository.findAllActiveNotices()).thenReturn(Collections.emptyList());
    when(noticeMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

    // When
    List<NoticeDto> result = noticeService.getActiveNotices();

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(noticeRepository).findAllActiveNotices();
    verify(noticeMapper).toDtoList(Collections.emptyList());
  }

  @Test
  @DisplayName("Should only return notices filtered by repository")
  void shouldOnlyReturnFilteredNotices() {
    // Given - repository already filters for active notices
    Notice activeNotice = new Notice();
    activeNotice.setNoticeId(1L);
    activeNotice.setNoticeSummary("Active Notice");
    activeNotice.setNoticeDetails("This notice is currently active");

    List<Notice> activeNotices = Collections.singletonList(activeNotice);

    NoticeDto dto = NoticeDto.builder()
        .noticeId(1L)
        .noticeSummary("Active Notice")
        .noticeDetails("This notice is currently active")
        .build();

    when(noticeRepository.findAllActiveNotices()).thenReturn(activeNotices);
    when(noticeMapper.toDtoList(activeNotices)).thenReturn(Collections.singletonList(dto));

    // When
    List<NoticeDto> result = noticeService.getActiveNotices();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getNoticeSummary()).isEqualTo("Active Notice");

    verify(noticeRepository).findAllActiveNotices();
    verify(noticeMapper).toDtoList(activeNotices);
  }
}
