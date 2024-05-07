package sws.NoticeBoard.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sws.NoticeBoard.controller.form.LoginForm;
import sws.NoticeBoard.controller.form.MemberSaveForm;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.domain.RefreshToken;
import sws.NoticeBoard.jwt.JwtTokenProvider;
import sws.NoticeBoard.service.LoginService;
import sws.NoticeBoard.service.RefreshTokenService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
	private final LoginService loginService;
	private final RefreshTokenService refreshTokenService;
	private final JwtTokenProvider jwtTokenProvider;

	@GetMapping("/login")
	public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
		return "login/loginForm";
	}

	@PostMapping("/login")
	public String login(
		@Validated @ModelAttribute LoginForm form,
		BindingResult bindingResult,
		@RequestParam(defaultValue = "/") String redirectURL,
		HttpServletResponse response) throws UnsupportedEncodingException {
		if (bindingResult.hasErrors()) {
			return "login/loginForm";
		}
		Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

		if (loginMember == null) {
			bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
			return "login/loginForm";
		}

		Token token = tokenGenerate(loginMember);
		String encodedValue = URLEncoder.encode("Bearer " + token.accessToken, "UTF-8");
		setCookie(response, encodedValue, 60 * 60 * 1);
		saveRefreshToken(loginMember, token);
		return "redirect:" + redirectURL;
	}

	@GetMapping("/login/new")
	public String memberSaveForm(@ModelAttribute MemberSaveForm form) {
		return "login/memberSaveForm";
	}

	@PostMapping("/login/new")
	public String memberSave(
		@Validated @ModelAttribute MemberSaveForm form,
		BindingResult bindingResult,
		@RequestParam(defaultValue = "/") String redirectURL,
		HttpServletResponse rep) {
		if (bindingResult.hasErrors()) {
			log.info("에러로 인한 이동");
			return "login/memberSaveForm";
		}
		try {
			Member member = loginService.join(form);
			Token token = tokenGenerate(member);
			String encodedValue = URLEncoder.encode("Bearer " + token.accessToken, "UTF-8");
			setCookie(rep, encodedValue, 60 * 60 * 1);
			// refresh token 정보 저장/수정
			saveRefreshToken(member, token);
		} catch (IllegalStateException e) {
			log.info("memberSaveException={}", e.getMessage());
			bindingResult.reject("memberSaveFail", e.getMessage());
			return "login/memberSaveForm";
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return "redirect:" + redirectURL;
	}

	private void saveRefreshToken(Member member, Token token) {
		RefreshToken rDTO = new RefreshToken();
		rDTO.setMember(member);
		rDTO.setToken("Bearer " + token.refreshToken);
		Date expirationDateFromToken = jwtTokenProvider.getExpirationDateFromToken(token.refreshToken);
		rDTO.setExpiredAt(expirationDateFromToken);
		refreshTokenService.save(rDTO);

	}

	private static void setCookie(HttpServletResponse rep, String encodedValue, int expiredTime) throws
		UnsupportedEncodingException {
		// JWT 쿠키 저장(쿠키 명 : token)
		Cookie cookie = new Cookie("swsToken", encodedValue);
		cookie.setPath("/");
		cookie.setMaxAge(expiredTime);
		// httoOnly 옵션을 추가해 서버만 쿠키에 접근할 수 있게 설정
		cookie.setHttpOnly(true);
		rep.addCookie(cookie);
	}

	private Token tokenGenerate(Member member) throws UnsupportedEncodingException {
		// 권한 map 저장
		Map<String, Object> rules = new HashMap<String, Object>();
		rules.put("rules", "USER");
		// JWT 발급
		Map<String, String> tokens = jwtTokenProvider.generateTokenSet(member.getLoginId(), rules);
		String accessToken = URLEncoder.encode(tokens.get("accessToken"), "utf-8");
		String refreshToken = URLEncoder.encode(tokens.get("refreshToken"), "utf-8");

		log.info("[JWT 발급] accessToken : " + accessToken);
		log.info("[JWT 발급] refreshToken : " + refreshToken);
		Token token = new Token(accessToken, refreshToken);
		return token;
	}

	private static class Token {
		public final String accessToken;
		public final String refreshToken;

		public Token(String accessToken, String refreshToken) {
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
		}
	}

	@PostMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response,
		@CookieValue(value = "swsToken", required = false) Cookie cookie) {
		log.info("로그아웃 실행");
		//    if(cookie != null){
		try {
			String encodedValue = URLEncoder.encode("", "UTF-8");
			setCookie(response, encodedValue, 0);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return "redirect:/";
	}
}
