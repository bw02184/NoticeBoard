package sws.NoticeBoard.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import net.coobird.thumbnailator.Thumbnails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sws.NoticeBoard.controller.form.BoardForm;
import sws.NoticeBoard.controller.form.PageRequestDTO;
import sws.NoticeBoard.cookie.CookieUtil;
import sws.NoticeBoard.domain.Board;
import sws.NoticeBoard.domain.Grade;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.service.BoardService;
import sws.NoticeBoard.service.MemberService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardController {
	private final BoardService boardService;
	private final MemberService memberService;
	private final CookieUtil cookieUtil;

	@GetMapping("/board/save")
	public String board(@ModelAttribute BoardForm form,
		Model model,
		@CookieValue(value = "swsToken", required = false) Cookie cookie) {
		String loginId = "";
		try {
			loginId = cookieUtil.getUsernameFromToken(cookie);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		Member member = memberService.memberLoad(loginId);
		model.addAttribute("member", member);
		return "board/boardSave";
	}

	@PostMapping("/board/save")
	public String boardSave(
		@Validated @ModelAttribute BoardForm form,
		BindingResult bindingResult,
		@CookieValue(value = "swsToken", required = false) Cookie cookie) {
		if (bindingResult.hasErrors()) {
			return "board/boardSave";
		}
		String loginId = "";
		try {
			loginId = cookieUtil.getUsernameFromToken(cookie);
			String fileName = getFileName(form.getContent());
			if (!fileName.equals("")) {
				String savePath = System.getProperty("user.dir") + "/src/main/resources/static/files/image/";
				StringBuffer sb = new StringBuffer(fileName);
				sb.insert(36, "_thumbnail");
				form.setThumbnailName(sb.toString());
				Thumbnails.of(new File(savePath + fileName))
					.size(160, 160)
					.toFile(new File(savePath + sb));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		boardService.save(form, loginId);

		return "redirect:/board/list";
	}

	private String getFileName(String content) {
		Pattern pattern = Pattern.compile(
			"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpeg|jpg|png|gif|bmp)");
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return matcher.group();
		}
		return "";
	}

	@GetMapping("/board/list")
	public String boardList(PageRequestDTO pageRequestDTO, Model model,
		@CookieValue(value = "swsToken", required = false) Cookie cookie) {
		String loginId = "";
		try {
			loginId = cookieUtil.getUsernameFromToken(cookie);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		Member member = memberService.memberLoad(loginId);
		model.addAttribute("result", boardService.getList(pageRequestDTO));
		model.addAttribute("member", member);
		return "board/boardList";
	}

	@GetMapping("/board/list/{id}")
	public String boardInfo(
		@PathVariable("id") Long id,
		HttpServletRequest request,
		HttpServletResponse response,
		Model model) {
		Board findBoard = boardService.findById(id);
		setCookie(id, request, response);
		model.addAttribute("post", findBoard);

		return "board/newBoardInfo";
	}

	private void setCookie(Long id, HttpServletRequest request, HttpServletResponse response) {
		// 쿠키를 이용해 로그인 시 한번만 조회수가 카운트 되도록 변경
		Cookie oldCookie = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals("boardView"))
					oldCookie = cookie;

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
	public String boardChange(
		@ModelAttribute BoardForm form, @PathVariable("id") Long id, Model model) {
		Board findBoard = boardService.findById(id);
		if (findBoard == null) {
			return "redirect:/board/list";
		}
		form.setId(findBoard.getId());
		form.setTitle(findBoard.getTitle());
		form.setContent(findBoard.getContent());
		model.addAttribute("post", findBoard);
		return "board/newBoardChange";
	}

	@PostMapping("/board/list/{id}/change")
	public String boardChange(
		@Validated @ModelAttribute BoardForm form,
		BindingResult bindingResult,
		@PathVariable("id") Long id,
		Model model,
		@CookieValue(value = "swsToken", required = false) Cookie cookie) {
		if (bindingResult.hasErrors()) {
			Board findBoard = boardService.findById(id);
			model.addAttribute("post", findBoard);
			return "board/newBoardChange";
		}
		String loginId = "";
		try {
			loginId = cookieUtil.getUsernameFromToken(cookie);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		boardService.update(form, loginId);

		return "redirect:/board/list";
	}

	@PostMapping("/board/list/{id}/delete")
	public String boardDelete(
		@ModelAttribute BoardForm form,
		@PathVariable("id") Long id,
		@CookieValue(value = "swsToken", required = false) Cookie cookie) {
		// url 조작을 방지하기 위해서 form.id와 PathVariable id를 비교한다.
		String loginId = "";
		try {
			loginId = cookieUtil.getUsernameFromToken(cookie);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		Member member = memberService.memberLoad(loginId);
		if (member.getGrade() == Grade.ADMIN || Objects.equals(form.getId(), id)) {
			boardService.delete(id, member.getLoginId());
			return "redirect:/board/list";
		} else
			return "redirect:/";
	}
}
