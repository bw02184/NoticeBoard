package sws.NoticeBoard.controller;

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
import sws.NoticeBoard.controller.form.MemberPWUpdateForm;
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
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
      BindingResult bindingResult,
      @RequestParam(defaultValue = "/") String redirectURL) {
    if (bindingResult.hasErrors()) {
      return "/member/memberInfo";
    }
    memberService.MemberInfoUpdate(loginMember.getLoginId(), form.getRealName(), form.getEmail());
    return "redirect:" + redirectURL;
  }

  @GetMapping("/member/password")
  public String memberPassword(@ModelAttribute MemberPWUpdateForm form) {
    log.info("memberPWUpdateForm");
    return "/member/memberPWUpdate";
  }

  @PostMapping("/member/password/update")
  public String memberPWUpdate(
      @Validated @ModelAttribute MemberPWUpdateForm form,
      @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
      BindingResult bindingResult,
      @RequestParam(defaultValue = "/") String redirectURL) {
    if (bindingResult.hasErrors()) {
      return "/member/memberPWUpdate";
    }
    form.setLoginId(loginMember.getLoginId());
    try {
      memberService.MemberPWUpdate(form);
    } catch (IllegalStateException e) {
      bindingResult.reject("pwCheck", e.getMessage());
      return "/member/memberPWUpdate";
    }
    return "redirect:" + redirectURL;
  }
}
