package sws.NoticeBoard.controller.form;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponse {

  private Long id; // 댓글 번호 (PK)
  private Long boardId; // 게시글 번호 (FK)
  private String content; // 내용
  private String writer; // 작성자
  private Boolean deleteYn; // 삭제 여부
  private LocalDateTime create_at; // 생성일시
  private LocalDateTime modify_at; // 최종 수정일시
}
