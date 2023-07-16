package sws.NoticeBoard.controller.form;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class MemberPwFindForm {
  @NotEmpty private String loginId;
  @NotEmpty private String realName;
  @NotEmpty @Email private String email;

  @Column(columnDefinition = "boolean default false")
  private Boolean emailConfirm;
}
