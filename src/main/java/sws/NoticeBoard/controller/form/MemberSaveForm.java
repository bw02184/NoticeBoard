package sws.NoticeBoard.controller.form;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class MemberSaveForm {
	@NotEmpty
	@Length(min = 4, max = 20)
	private String loginId;

	@NotEmpty
	private String realName;

	@NotEmpty
	@Length(min = 4, max = 20)
	private String password;

	@NotEmpty
	@Length(min = 4, max = 20)
	private String password2;

	@NotEmpty
	@Email
	private String email;
	@Email
	private String checkedEmail;

	@Column(columnDefinition = "boolean default false")
	private Boolean emailConfirm;
}
