package sws.NoticeBoard.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sws.NoticeBoard.controller.form.LoginForm;
import sws.NoticeBoard.controller.form.MemberSaveForm;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.service.LoginService;
import sws.NoticeBoard.session.SessionConst;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
  private final LoginService loginService;

  @GetMapping("/login")
  public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
    log.info("login page");
    return "login/loginForm";
  }

  @PostMapping("/login")
  public String login(
      @Validated @ModelAttribute LoginForm form,
      BindingResult bindingResult,
      @RequestParam(defaultValue = "/") String redirectURL,
      HttpServletRequest request) {
    if (bindingResult.hasErrors()) {
      return "login/loginForm";
    }
    Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

    if (loginMember == null) {
      bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
      return "login/loginForm";
    }
    // 로그인 성공 처리
    HttpSession session = request.getSession();
    session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

    return "redirect:" + redirectURL;
  }

  @GetMapping("/login/new")
  public String memberSaveForm(@ModelAttribute MemberSaveForm form) {
    log.info("memberSaveForm");
    return "login/memberSaveForm";
  }

  @PostMapping("/login/new")
  public String memberSave(
      @Validated @ModelAttribute MemberSaveForm form,
      BindingResult bindingResult,
      @RequestParam(defaultValue = "/") String redirectURL) {
    if (bindingResult.hasErrors()) {
      return "login/memberSaveForm";
    }
    loginService.join(form);

    return "redirect:" + redirectURL;
  }
}
