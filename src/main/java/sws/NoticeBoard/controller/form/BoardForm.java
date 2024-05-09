package sws.NoticeBoard.controller.form;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BoardForm {
	private Long id;

	@NotEmpty
	@Column(length = 100, nullable = false)
	private String title; // 제목

	@Column(length = 5000)
	private String content; // 내용

	private String thumbnailName;

	@Column(columnDefinition = "boolean default false")
	private boolean noticeYn; //공지글 여부

}
