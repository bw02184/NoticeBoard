package sws.NoticeBoard.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sws.NoticeBoard.cookie.CookieUtil;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.repository.MemberRepository;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {
	private final CookieUtil cookieUtil;
	private final MemberRepository memberRepository;

	@GetMapping
	public String homeLogin(
		Model model,
		@CookieValue(value = "swsToken", required = false) Cookie cookie) {
		if (cookie == null) {
			return "home";
		}
		try {
			String loginId = cookieUtil.getUsernameFromToken(cookie);
			Member member = memberRepository.findByLoginId(loginId);
			model.addAttribute("member", member);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return "loginHome";
	}
}
