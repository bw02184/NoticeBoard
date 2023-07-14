package sws.NoticeBoard.controller.form;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MemberPWUpdateForm {
  private String loginId;

  @NotEmpty
  @Length(min = 4, max = 20)
  private String password;

  @NotEmpty
  @Length(min = 4, max = 20)
  private String newPassword;

  @NotEmpty
  @Length(min = 4, max = 20)
  private String newPassword2;
}
