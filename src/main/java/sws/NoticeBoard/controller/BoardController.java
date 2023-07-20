package sws.NoticeBoard.controller;

import java.util.List;
import java.util.Objects;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import sws.NoticeBoard.controller.form.BoardForm;
import sws.NoticeBoard.domain.Board;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.service.BoardService;
import sws.NoticeBoard.session.SessionConst;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardController {
  private final BoardService boardService;

  @GetMapping("/board/save")
  public String board(@ModelAttribute BoardForm form) {
    return "board/boardSave";
  }

  @PostMapping("/board/save")
  public String boardSave(
      @Validated @ModelAttribute BoardForm form,
      BindingResult bindingResult,
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) {
    if (bindingResult.hasErrors()) {
      return "board/boardSave";
    }
    boardService.save(form, loginMember.getLoginId());

    return "redirect:/board/list";
  }

  @GetMapping("/board/list")
  public String boardList(Model model) {
    List<Board> boards = boardService.findByAll();
    model.addAttribute("boards", boards);
    return "board/boardList";
  }

  @GetMapping("/board/list/{id}")
  public String boardInfo(
      @ModelAttribute BoardForm form,
      @PathVariable("id") Long id,
      HttpServletRequest request,
      HttpServletResponse response) {
    Board findBoard = boardService.findById(id);
    setCookie(id, request, response);
    form.setContent(findBoard.getContent());
    form.setTitle(findBoard.getTitle());

    return "board/boardInfo";
  }

  private void setCookie(Long id, HttpServletRequest request, HttpServletResponse response) {
    // 쿠키를 이용해 로그인 시 한번만 조회수가 카운트 되도록 변경
    Cookie oldCookie = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null)
      for (Cookie cookie : cookies) if (cookie.getName().equals("boardView")) oldCookie = cookie;

    if (oldCookie != null) {
      if (!oldCookie.getValue().contains("[" + id.toString() + "]")) {
        boardService.viewCount(id);
        oldCookie.setValue(oldCookie.getValue() + "_[" + id + "]");
        oldCookie.setPath("/");
        oldCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(oldCookie);
      }
    } else {
      boardService.viewCount(id);
      Cookie newCookie = new Cookie("boardView", "[" + id + "]");
      newCookie.setPath("/");
      newCookie.setMaxAge(60 * 60 * 24);
      response.addCookie(newCookie);
    }
  }

  @GetMapping("/board/list/{id}/change")
  public String boardChange(@ModelAttribute BoardForm form, @PathVariable("id") Long id) {
    Board findBoard = boardService.findById(id);
    form.setId(findBoard.getId());
    form.setTitle(findBoard.getTitle());
    form.setContent(findBoard.getContent());
    return "board/boardChange";
  }

  @PostMapping("/board/list/{id}/change")
  public String boardChange(
      @Validated @ModelAttribute BoardForm form, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "board/boardChange";
    }
    log.info("{}{}{}", form.getId(), form.getTitle(), form.getContent());
    boardService.update(form);

    return "redirect:/board/list";
  }

  @PostMapping("/board/list/{id}/delete")
  public String boardDelete(@ModelAttribute BoardForm form, @PathVariable("id") Long id) {
    // url 조작을 방지하기 위해서 form.id와 PathVariable id를 비교한다.
    if (Objects.equals(form.getId(), id)) {
      boardService.delete(id);
      return "redirect:/board/list";
    } else return "redirect:/";
  }
}
