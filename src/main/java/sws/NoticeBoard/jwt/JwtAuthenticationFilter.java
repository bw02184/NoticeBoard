package sws.NoticeBoard.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;
import sws.NoticeBoard.controller.form.AdminDTO;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.repository.MemberJpaRepository;
import sws.NoticeBoard.repository.MemberRepository;
import sws.NoticeBoard.service.MemberService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberJpaRepository memberJpaRepository;

    private static final List<String> EXCLUDE_URL =
            Collections.unmodifiableList(
                    Arrays.asList(
                            "/logout",
                            "/",
                            "/login",
                            "/login/new",
                            "/login/mail/confirm",
                            "/member/id/find",
                            "/member/findId",
                            "/member/password/find",
                            "/member/searchPw/change",
                            "/css/**",
                            "/*.ico",
                            "/emailCheck.js",
                            "/js/jquery-3.6.0.min.js",
                            "/error"
                    ));

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // jwt cookie 사용 시 해당 코드를 사용하여 쿠키에서 토큰을 받아오도록 함
        String token = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("swsToken"))
                .findFirst() .map(Cookie::getValue)
                .orElse(null);

        String memberLoginId = null;
        String jwtToken = null;
        if(token != null)
            token = URLDecoder.decode( token, "UTF-8") ;
        // Bearer token인 경우 JWT 토큰 유효성 검사 진행
        if (token != null && token.startsWith("Bearer ")) {
            jwtToken = token.substring(7);
            try {
                memberLoginId = jwtTokenProvider.getUsernameFromToken(jwtToken);
                System.out.println("memberLoginId = " + memberLoginId);
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
        return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }

}