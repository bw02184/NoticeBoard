package sws.NoticeBoard.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Board {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;

  @NotEmpty
  @Column(length = 100, nullable = false)
  private String title; // 제목

  @Column(length = 5000)
  private String content; // 내용

  private Long viewCount; // 조회수

  @Column(columnDefinition = "boolean default false")
  private Boolean noticeYn; // 공지글 여부

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime create_at;

  @LastModifiedDate private LocalDateTime modify_at;

  // ==연관관계 메서드==//
  public void setMember(Member member) {
    if (this.member != null) {
      this.member.getBoards().remove(this);
    }
    this.member = member;
    member.getBoards().add(this);
  }
}
