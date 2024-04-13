package sws.NoticeBoard.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sws.NoticeBoard.controller.form.CommentRequest;
import sws.NoticeBoard.controller.form.CommentResponse;
import sws.NoticeBoard.cookie.CookieUtil;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.service.BoardService;
import sws.NoticeBoard.service.CommentService;
import sws.NoticeBoard.session.SessionConst;

import javax.servlet.http.Cookie;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentController {
  private final CommentService commentService;
  private final CookieUtil cookieUtil;

  @PostMapping("/board/list/{id}/comments")
  public CommentResponse boardComment(
      @RequestBody CommentRequest params,
      @PathVariable("id") Long id,
      Model model,
      @CookieValue(value = "swsToken", required = false) Cookie cookie) {
      try {
        String loginId = cookieUtil.getUsernameFromToken(cookie);
        commentService.save(id, loginId, params.getContent());
      } catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e);
      }
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
      @CookieValue(value = "swsToken", required = false) Cookie cookie) {
      String loginId = "";
      try {
        loginId = cookieUtil.getUsernameFromToken(cookie);
      } catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e);
      }
      return commentService.findCommentById(commentId, loginId);
  }

  // 기존 댓글 수정
  @PatchMapping("/board/list/{id}/comments/{commentId}")
  public CommentResponse updateComment(
      @PathVariable("id") final Long boardId,
      @PathVariable("commentId") final Long commentId,
      @RequestBody final CommentRequest params,
      @CookieValue(value = "swsToken", required = false) Cookie cookie) {
    commentService.updateComment(params);
    String loginId = "";
    try {
      loginId = cookieUtil.getUsernameFromToken(cookie);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    return commentService.findCommentById(commentId, loginId);
  }

  // 댓글 삭제
  @DeleteMapping("/board/list/{id}/comments/{commentId}")
  public Long deleteComment(
      @PathVariable("id") final Long boardId,
      @PathVariable("commentId") final Long commentId,
      @CookieValue(value = "swsToken", required = false) Cookie cookie) {
    String loginId = "";
    try {
      loginId = cookieUtil.getUsernameFromToken(cookie);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    return commentService.deleteComment(commentId, loginId);
  }
}
