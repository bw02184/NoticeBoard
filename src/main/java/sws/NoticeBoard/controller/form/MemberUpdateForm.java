package sws.NoticeBoard.controller.form;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class MemberUpdateForm {

	private String loginId;
	@NotEmpty
	private String realName;
	@NotEmpty
	@Email
	private String email;
	@Email
	private String checkedEmail;

	@Column(columnDefinition = "boolean default false")
	private Boolean emailConfirm;
}
