package sws.NoticeBoard.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;
import sws.NoticeBoard.controller.form.AdminDTO;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.repository.MemberJpaRepository;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberJpaRepository memberJpaRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("do filter 작동중");
        // jwt cookie 사용 시 해당 코드를 사용하여 쿠키에서 토큰을 받아오도록 함
        String token = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("swsToken"))
                .findFirst() .map(Cookie::getValue)
                .orElse(null);

        String memberLoginId = null;
        String jwtToken = null;
        if(token != null)
            token = URLDecoder.decode( token, "UTF-8");
        // Bearer token인 경우 JWT 토큰 유효성 검사 진행
        if (token != null && token.startsWith("Bearer ")) {
            jwtToken = token.substring(7);
            try {
                memberLoginId = jwtTokenProvider.getUsernameFromToken(jwtToken);
            } catch (MalformedJwtException e) {
                log.error("Invalid JWT token: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                log.error("JWT token is expired: {}", e.getMessage());
            } catch (UnsupportedJwtException e) {
                log.error("JWT token is unsupported: {}", e.getMessage());
            } catch (IllegalArgumentException e) {
                log.error("JWT claims string is empty: {}", e.getMessage());
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        // token 검증이 되고 인증 정보가 존재하지 않는 경우 spring security 인증 정보 저장
        if(memberLoginId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Member member = new Member();
            try {
                member = memberJpaRepository.findByLoginId(memberLoginId).orElse(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            AdminDTO adminDTO = new AdminDTO();
            adminDTO.setRole(member.getRoles());
            adminDTO.setAuthorities(member.getAuthorities());
            adminDTO.setId(member.getId());
            adminDTO.setLoginId(member.getLoginId());
            adminDTO.setPassword(member.getPassword());

            if(jwtTokenProvider.validateToken(jwtToken)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(adminDTO, null ,adminDTO.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        try {
            if(memberLoginId != null) {
                Date expirationDateFromToken = jwtTokenProvider.getExpirationDateFromToken(jwtToken);
                LocalDateTime localDateTime = LocalDateTime.ofInstant(expirationDateFromToken.toInstant(), ZoneId.systemDefault());
                if(localDateTime.minusMinutes(30).isBefore(LocalDateTime.now())){
                    String accessToken = expiredAccessTokenToRegenerateToken(response, memberLoginId);
                    log.info("[JwtRequestFilter] accessToken 재발급 {}", accessToken);
                }
            }
        }catch (Exception e) {
            log.error("[JwtRequestFilter] accessToken 재발급 체크 중 문제 발생 : {}", e.getMessage());
        }

        // accessToken 인증이 되었다면 refreshToken 재발급이 필요한 경우 재발급
        try {
            if(memberLoginId != null) {
                jwtTokenProvider.reGenerateRefreshToken(memberLoginId);
            }
        }catch (Exception e) {
            log.error("[JwtRequestFilter] refreshToken 재발급 체크 중 문제 발생 : {}", e.getMessage());
        }

        filterChain.doFilter(request,response);
    }

    // Filter에서 제외할 URL 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] exclude_url = {
                "/",
                "/logout",
                "/login",
                "/login/new",
                "/login/mail/confirm",
                "/member/id/find",
                "/member/findId",
                "/member/password/find",
                "/member/searchPw/change",
                "/css/**",
                "/css/bootstrap.min.css",
                "/css/jumbotron-narrow.css",
                "/css/bootstrap.min.css.map",
                "/*.ico",
                "/favicon.ico",
                "/emailCheck.js",
                "/js/jquery-3.6.0.min.js",
                "/error"};
        String path = request.getRequestURI();
        return Arrays.asList(exclude_url).contains(path);
    }

    private String expiredAccessTokenToRegenerateToken(HttpServletResponse response, String loginId) throws UnsupportedEncodingException {
        String token = jwtTokenProvider.generateAccessToken(loginId);
        String encodedValue = URLEncoder.encode( "Bearer " + token, "UTF-8" );
        setCookie(response, encodedValue, 60*60*1);
        return token;
    }

    private static void setCookie(HttpServletResponse rep, String encodedValue, int expiredTime) throws UnsupportedEncodingException {
        // JWT 쿠키 저장(쿠키 명 : token)
        Cookie cookie = new Cookie("swsToken", encodedValue);
        cookie.setPath("/");
        cookie.setMaxAge(expiredTime);
        // httoOnly 옵션을 추가해 서버만 쿠키에 접근할 수 있게 설정
        cookie.setHttpOnly(true);
        rep.addCookie(cookie);
    }

}