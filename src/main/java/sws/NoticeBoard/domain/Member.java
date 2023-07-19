package sws.NoticeBoard.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Member {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty private String loginId;
  @NotEmpty private String realName;
  @NotEmpty private String password;

  @OneToMany(mappedBy = "member")
  private List<Board> boards = new ArrayList<>();

  @NotEmpty @Email private String email;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  private Boolean isAdmin;
}
