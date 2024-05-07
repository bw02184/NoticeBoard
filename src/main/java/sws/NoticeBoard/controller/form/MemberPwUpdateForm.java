package sws.NoticeBoard.controller.form;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class MemberPwUpdateForm {
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
