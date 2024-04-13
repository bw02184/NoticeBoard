package sws.NoticeBoard.cookie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sws.NoticeBoard.jwt.JwtTokenProvider;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RequiredArgsConstructor
@Component
public class CookieUtil {
    private final JwtTokenProvider jwtTokenProvider;
    public String getUsernameFromToken(Cookie cookie) throws UnsupportedEncodingException {
        return jwtTokenProvider.getUsernameFromToken(URLDecoder.decode(cookie.getValue().substring(7), "UTF-8"));
    }
}
