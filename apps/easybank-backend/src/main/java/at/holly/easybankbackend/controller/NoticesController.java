package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Notice;
import at.holly.easybankbackend.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticesController{

  private final NoticeRepository noticeRepository;

  @GetMapping("/notices")
  public List<Notice> getNotices () {
    return noticeRepository.findAllActiveNotices();
  }

}
