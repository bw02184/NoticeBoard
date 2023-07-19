package sws.NoticeBoard.controller.form;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sws.NoticeBoard.domain.Member;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BoardListForm {
  private Long id;
  private Member member;

  @NotEmpty
  @Column(length = 100, nullable = false)
  private String title; // 제목

  @Column(length = 5000)
  private String content; // 내용

  private Long viewCount; // 조회수

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime create_at;

  @LastModifiedDate private LocalDateTime modify_at;
}
