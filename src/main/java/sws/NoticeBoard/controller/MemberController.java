package sws.NoticeBoard.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sws.NoticeBoard.controller.form.MemberDeleteForm;
import sws.NoticeBoard.controller.form.MemberIdFindForm;
import sws.NoticeBoard.controller.form.MemberPwFindForm;
import sws.NoticeBoard.controller.form.MemberPwUpdateForm;
import sws.NoticeBoard.controller.form.MemberSearchPwChangeForm;
import sws.NoticeBoard.controller.form.MemberUpdateForm;
import sws.NoticeBoard.cookie.CookieUtil;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.service.MemberService;
import sws.NoticeBoard.service.RefreshTokenService;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {
	private final MemberService memberService;
	private final RefreshTokenService refreshTokenService;
	private final CookieUtil cookieUtil;

	@GetMapping("/member")
	public String memberInfo(
		@CookieValue(value = "swsToken", required = false) Cookie cookie, Model model) {
		String loginId = null;
		try {
			loginId = cookieUtil.getUsernameFromToken(cookie);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		Member member =
			memberService.MemberSearch(loginId); // 회원 정보를 변환 하고 난 후 정보 정확성을 위하여 회원조회
		MemberUpdateForm form = new MemberUpdateForm();
		form.setRealName(member.getRealName());
		form.setEmail(member.getEmail());
		form.setLoginId(member.getLoginId());
		model.addAttribute("memberUpdateForm", form);
		return "member/memberInfo";
	}

	@PostMapping("/member/update")
	public String memberUpdate(
		@Validated @ModelAttribute MemberUpdateForm form,
		BindingResult bindingResult,
		@CookieValue(value = "swsToken", required = false) Cookie cookie,
		@RequestParam(defaultValue = "/") String redirectURL) {
		if (bindingResult.hasErrors()) {
			return "member/memberInfo";
		}
		try {
			String loginId = cookieUtil.getUsernameFromToken(cookie);
			memberService.memberInfoUpdate(loginId,
				form.getRealName(),
				form.getEmail(),
				form.getCheckedEmail(),
				form.getEmailConfirm());
		} catch (IllegalStateException e) {
			bindingResult.reject("emailCheck", e.getMessage());
			return "member/memberInfo";
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return "redirect:" + redirectURL;
	}

	@GetMapping("/member/password")
	public String memberPassword(@ModelAttribute MemberPwUpdateForm form) {
		return "member/memberPWUpdate";
	}

	@PostMapping("/member/password/update")
	public String memberPWUpdate(
		@Validated @ModelAttribute MemberPwUpdateForm form,
		BindingResult bindingResult,
		@CookieValue(value = "swsToken", required = false) Cookie cookie,
		@RequestParam(defaultValue = "/") String redirectURL) {
		if (bindingResult.hasErrors()) {
			log.info("에러로 인한 이동");
			return "member/memberPWUpdate";
		}
		try {
			String loginId = cookieUtil.getUsernameFromToken(cookie);
			form.setLoginId(loginId);

			memberService.memberPWUpdate(form);
		} catch (IllegalStateException e) {
			bindingResult.reject("pwCheck", e.getMessage());
			return "member/memberPWUpdate";
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return "redirect:" + redirectURL;
	}

	@GetMapping("/member/id/find")
	public String memberId(@ModelAttribute MemberIdFindForm form) {
		return "member/memberIdFind";
	}

	@PostMapping("/member/id/find")
	public String memberIdFind(
		@Validated @ModelAttribute MemberIdFindForm form, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "member/memberIdFind";
		}
		String loginId = "";
		try {
			loginId = memberService.memberIdFind(form);
		} catch (IllegalStateException e) {
			bindingResult.reject("emailCheck", e.getMessage());
			return "member/memberIdFind";
		}
		return "redirect:/member/findId?loginId=" + loginId;
	}

	@GetMapping("/member/findId")
	public String findId(@RequestParam String loginId, Model model) {
		model.addAttribute("loginId", loginId);
		return "member/findId";
	}

	@GetMapping("/member/password/find")
	public String memberPassword(@ModelAttribute MemberPwFindForm form) {
		return "member/memberPwFind";
	}

	@PostMapping("/member/password/find")
	public String memberPasswordFind(
		@Validated @ModelAttribute MemberPwFindForm form,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "member/memberPwFind";
		}
		Member member;
		try {
			member = memberService.memberPwFind(form);
		} catch (IllegalStateException e) {
			bindingResult.reject("pwFindCheck", e.getMessage());
			return "member/memberPwFind";
		}
		redirectAttributes.addFlashAttribute("member", member);
		return "redirect:/member/searchPw/change";
	}

	@GetMapping("/member/searchPw/change")
	public String memberSearchPassword(
		@ModelAttribute MemberSearchPwChangeForm form, @ModelAttribute("member") Member member) {
		form.setLoginId(member.getLoginId());
		return "member/memberSearchPwChange";
	}

	@PostMapping("/member/searchPw/change")
	public String memberSearchPasswordChange(
		@Validated @ModelAttribute MemberSearchPwChangeForm form, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "member/memberSearchPwChange";
		}
		try {
			memberService.memberSearchPwChange(form);
		} catch (IllegalStateException e) {
			bindingResult.reject("pwCheck", e.getMessage());
			return "member/memberSearchPwChange";
		} catch (RuntimeException e) {
			log.info("비밀번호 변경 중 오류가 발생했습니다. 다시 실행해 주세요", e);
			return "home";
		}
		return "redirect:/";
	}

	@GetMapping("/member/delete")
	public String memberDelete(
		@ModelAttribute MemberDeleteForm form,
		@CookieValue(value = "swsToken", required = false) Cookie cookie) {
		try {
			String loginId = cookieUtil.getUsernameFromToken(cookie);
			form.setLoginId(loginId);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return "member/memberDelete";
	}

	@PostMapping("/member/delete")
	public String memberDelete(
		@Validated @ModelAttribute MemberDeleteForm form,
		BindingResult bindingResult,
		@CookieValue(value = "swsToken", required = false) Cookie cookie,
		HttpServletResponse response) {
		// form 화면에서 강제로 아이디를 변경했을 경우를 대비해서 세션에서 로그인 아이디를 얻어 와서 form 데이터에 넣어준다.
		try {
			String loginId = cookieUtil.getUsernameFromToken(cookie);
			form.setLoginId(loginId);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		if (bindingResult.hasErrors()) {
			return "member/memberDelete";
		}
		try {
			memberService.memberDelete(form);
		} catch (IllegalStateException e) {
			bindingResult.reject("pwCheck", e.getMessage());
			return "member/memberDelete";
		}
		cookie.setPath("/");
		cookie.setMaxAge(0);
		// httoOnly 옵션을 추가해 서버만 쿠키에 접근할 수 있게 설정
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
		return "redirect:/";
	}

}
