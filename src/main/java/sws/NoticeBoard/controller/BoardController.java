package sws.NoticeBoard.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
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
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
      @RequestParam(defaultValue = "/") String redirectURL) {
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
  public String boardInfo(@ModelAttribute BoardForm form, @PathVariable("id") Long id) {
    Board findBoard = boardService.findById(id);
    form.setContent(findBoard.getContent());
    form.setTitle(findBoard.getTitle());

    return "board/boardInfo";
  }
}
