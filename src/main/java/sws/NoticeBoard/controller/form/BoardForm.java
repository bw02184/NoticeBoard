package sws.NoticeBoard.controller.form;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BoardForm {
  @NotEmpty
  @Column(length = 100, nullable = false)
  private String title; // 제목

  @Column(length = 5000)
  private String content; // 내용
}
