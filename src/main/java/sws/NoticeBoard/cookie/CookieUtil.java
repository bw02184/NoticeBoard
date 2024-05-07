package sws.NoticeBoard.cookie;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sws.NoticeBoard.jwt.JwtTokenProvider;

@RequiredArgsConstructor
@Component
public class CookieUtil {
	private final JwtTokenProvider jwtTokenProvider;

	public String getUsernameFromToken(Cookie cookie) throws UnsupportedEncodingException {
		return jwtTokenProvider.getUsernameFromToken(URLDecoder.decode(cookie.getValue().substring(7), "UTF-8"));
	}
}
