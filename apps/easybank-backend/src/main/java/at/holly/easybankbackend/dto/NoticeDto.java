package at.holly.easybankbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * Notice Data Transfer Object
 * Used for API responses to avoid exposing entity internals
 * Note: createDt and updateDt are excluded from responses for security
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDto {

    private Long noticeId;
    private String noticeSummary;
    private String noticeDetails;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date noticBegDt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date noticEndDt;
}
