package sws.NoticeBoard.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sws.NoticeBoard.controller.form.MemberDeleteForm;
import sws.NoticeBoard.controller.form.MemberIdFindForm;
import sws.NoticeBoard.controller.form.MemberPwFindForm;
import sws.NoticeBoard.controller.form.MemberPwUpdateForm;
import sws.NoticeBoard.controller.form.MemberSearchPwChangeForm;
import sws.NoticeBoard.controller.form.MemberUpdateForm;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.service.MemberService;
import sws.NoticeBoard.session.SessionConst;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {
  private final MemberService memberService;

  @GetMapping("/member")
  public String memberInfo(
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
      Model model) {
    log.info("memberUpdateForm");
    Member member =
        memberService.MemberSearch(loginMember.getLoginId()); // 회원 정보를 변환 하고 난 후 정보 정확성을 위하여 회원조회
    MemberUpdateForm form = new MemberUpdateForm();
    form.setRealName(member.getRealName());
    form.setEmail(member.getEmail());
    form.setLoginId(member.getLoginId());
    model.addAttribute("memberUpdateForm", form);
    return "/member/memberInfo";
  }

  @PostMapping("/member/update")
  public String memberUpdate(
      @Validated @ModelAttribute MemberUpdateForm form,
      BindingResult bindingResult,
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
      @RequestParam(defaultValue = "/") String redirectURL) {
    if (bindingResult.hasErrors()) {
      return "/member/memberInfo";
    }
    try {
      memberService.memberInfoUpdate(
          loginMember.getLoginId(), form.getRealName(), form.getEmail(), form.getEmailConfirm());
    } catch (IllegalStateException e) {
      bindingResult.reject("emailCheck", e.getMessage());
      return "/member/memberInfo";
    }
    return "redirect:" + redirectURL;
  }

  @GetMapping("/member/password")
  public String memberPassword(@ModelAttribute MemberPwUpdateForm form) {
    log.info("memberPWUpdateForm");
    return "/member/memberPWUpdate";
  }

  @PostMapping("/member/password/update")
  public String memberPWUpdate(
      @Validated @ModelAttribute MemberPwUpdateForm form,
      BindingResult bindingResult,
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
      @RequestParam(defaultValue = "/") String redirectURL) {
    if (bindingResult.hasErrors()) {
      log.info("에러로 인한 이동");
      return "/member/memberPWUpdate";
    }
    form.setLoginId(loginMember.getLoginId());
    try {
      memberService.memberPWUpdate(form);
    } catch (IllegalStateException e) {
      bindingResult.reject("pwCheck", e.getMessage());
      return "/member/memberPWUpdate";
    }
    return "redirect:" + redirectURL;
  }

  @GetMapping("/member/id/find")
  public String memberId(@ModelAttribute MemberIdFindForm form) {
    return "/member/memberIdFind";
  }

  @PostMapping("/member/id/find")
  public String memberIdFind(
      @Validated @ModelAttribute MemberIdFindForm form, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "/member/memberIdFind";
    }
    String loginId = "";
    try {
      loginId = memberService.memberIdFind(form);
    } catch (IllegalStateException e) {
      bindingResult.reject("emailCheck", e.getMessage());
      return "/member/memberIdFind";
    }
    return "redirect:/member/findId?loginId=" + loginId;
  }

  @GetMapping("/member/findId")
  public String findId(@RequestParam String loginId, Model model) {
    model.addAttribute("loginId", loginId);
    return "/member/findId";
  }

  @GetMapping("/member/password/find")
  public String memberPassword(@ModelAttribute MemberPwFindForm form) {
    return "/member/memberPwFind";
  }

  @PostMapping("/member/password/find")
  public String memberPasswordFind(
      @Validated @ModelAttribute MemberPwFindForm form,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      return "/member/memberPwFind";
    }
    Member member;
    try {
      member = memberService.memberPwFind(form);
    } catch (IllegalStateException e) {
      bindingResult.reject("pwFindCheck", e.getMessage());
      return "/member/memberPwFind";
    }
    redirectAttributes.addFlashAttribute("member", member);
    return "redirect:/member/searchPw/change";
  }

  @GetMapping("/member/searchPw/change")
  public String memberSearchPassword(
      @ModelAttribute MemberSearchPwChangeForm form, @ModelAttribute("member") Member member) {
    form.setLoginId(member.getLoginId());
    return "/member/memberSearchPwChange";
  }

  @PostMapping("/member/searchPw/change")
  public String memberSearchPasswordChange(
      @Validated @ModelAttribute MemberSearchPwChangeForm form, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "/member/memberSearchPwChange";
    }
    try {
      memberService.memberSearchPwChange(form);
    } catch (IllegalStateException e) {
      bindingResult.reject("pwCheck", e.getMessage());
      return "/member/memberSearchPwChange";
    } catch (RuntimeException e) {
      log.info("비밀번호 변경 중 오류가 발생했습니다. 다시 실행해 주세요", e);
      return "/home";
    }
    return "redirect:/";
  }

  @GetMapping("/member/delete")
  public String memberDelete(
      @ModelAttribute MemberDeleteForm form,
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) {
    form.setLoginId(loginMember.getLoginId());
    return "/member/memberDelete";
  }

  @PostMapping("/member/delete")
  public String memberDelete(
      @Validated @ModelAttribute MemberDeleteForm form,
      BindingResult bindingResult,
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
      HttpServletRequest request) {
    // form 화면에서 강제로 아이디를 변경했을 경우를 대비해서 세션에서 로그인 아이디를 얻어 와서 form 데이터에 넣어준다.
    form.setLoginId(loginMember.getLoginId());
    if (bindingResult.hasErrors()) {
      return "/member/memberDelete";
    }
    try {
      memberService.memberDelete(form);
    } catch (IllegalStateException e) {
      bindingResult.reject("pwCheck", e.getMessage());
      return "/member/memberDelete";
    }
    // 로그아웃
    HttpSession session = request.getSession(false);
    session.invalidate();
    return "redirect:/";
  }
}
