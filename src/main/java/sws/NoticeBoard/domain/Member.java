package sws.NoticeBoard.domain;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {
  @Id @GeneratedValue private Long id;

  @NotEmpty private String loginId;
  @NotEmpty private String realName;
  @NotEmpty private String password;
  @NotEmpty @Email private String email;

  private LocalDateTime createdAt;

  private Boolean isAdmin;
}
