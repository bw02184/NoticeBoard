package sws.NoticeBoard.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import sws.NoticeBoard.controller.form.CommentRequest;
import sws.NoticeBoard.controller.form.CommentResponse;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.service.BoardService;
import sws.NoticeBoard.service.CommentService;
import sws.NoticeBoard.session.SessionConst;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentController {
  private final BoardService boardService;
  private final CommentService commentService;

  @PostMapping("/board/list/{id}/comments")
  public CommentResponse boardComment(
      @RequestBody CommentRequest params,
      @PathVariable("id") Long id,
      Model model,
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) {
    commentService.save(id, loginMember.getLoginId(), params.getContent());
    return commentService.findById(id);
  }

  // 댓글 리스트 조회
  @GetMapping("/board/list/{id}/comments")
  public List<CommentResponse> findAllComment(@PathVariable final Long id) {
    return commentService.findByAll(id);
  }

  // 댓글 상세정보 조회
  @GetMapping("/board/list/{id}/comments/{commentId}")
  public CommentResponse findCommentById(
      @PathVariable("id") final Long boardId,
      @PathVariable("commentId") final Long commentId,
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) {
    return commentService.findCommentById(commentId, loginMember.getLoginId());
  }

  // 기존 댓글 수정
  @PatchMapping("/board/list/{id}/comments/{commentId}")
  public CommentResponse updateComment(
      @PathVariable("id") final Long boardId,
      @PathVariable("commentId") final Long commentId,
      @RequestBody final CommentRequest params,
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) {
    commentService.updateComment(params);
    return commentService.findCommentById(commentId, loginMember.getLoginId());
  }

  // 댓글 삭제
  @DeleteMapping("/board/list/{id}/comments/{commentId}")
  public Long deleteComment(
      @PathVariable("id") final Long boardId,
      @PathVariable("commentId") final Long commentId,
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) {
    return commentService.deleteComment(commentId, loginMember.getLoginId());
  }
}
