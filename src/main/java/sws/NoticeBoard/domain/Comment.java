package sws.NoticeBoard.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content; // 댓글 내용

	@CreatedDate
	private LocalDateTime create_at;

	@Column(name = "modified_date")
	@LastModifiedDate
	private LocalDateTime modify_at;

	@Column(columnDefinition = "boolean default false")
	private Boolean deleteYn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id")
	private Board board;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member; // 작성자

	public void setBoard(Board board) {
		if (this.board != null) {
			this.board.getComments().remove(this);
		}
		this.board = board;
		board.getComments().add(this);
	}

	public void setMember(Member member) {
		if (this.member != null) {
			this.member.getComments().remove(this);
		}
		this.member = member;
		member.getComments().add(this);
	}
}
