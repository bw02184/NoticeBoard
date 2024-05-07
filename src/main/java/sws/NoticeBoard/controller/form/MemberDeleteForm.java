package sws.NoticeBoard.controller.form;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class MemberDeleteForm {

	@NotEmpty
	private String loginId;

	@NotEmpty
	@Length(min = 4, max = 20)
	private String password;
}
