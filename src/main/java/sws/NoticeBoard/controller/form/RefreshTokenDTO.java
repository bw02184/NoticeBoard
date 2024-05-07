package sws.NoticeBoard.controller.form;

import lombok.Data;

@Data
public class RefreshTokenDTO {
	private Long refreshTokenId;
	private Long MemberId;
	private String refreshToken;
}
