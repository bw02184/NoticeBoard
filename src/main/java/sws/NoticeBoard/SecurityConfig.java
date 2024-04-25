package sws.NoticeBoard;

import javax.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import sws.NoticeBoard.jwt.JwtAuthenticationFilter;
import sws.NoticeBoard.jwt.JwtTokenProvider;
import sws.NoticeBoard.repository.MemberJpaRepository;
import sws.NoticeBoard.repository.MemberRepository;
import sws.NoticeBoard.service.MemberService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberJpaRepository memberJpaRepository;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic().disable()
                .csrf().disable()
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                // 해당 API에 대해서는 모든 요청을 허가
                .requestMatchers(new AntPathRequestMatcher("/error"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/*.ico"),
                        new AntPathRequestMatcher("/logout"),
                        new AntPathRequestMatcher("/"),
                        new AntPathRequestMatcher("/login"),
                        new AntPathRequestMatcher("/login/new"),
                        new AntPathRequestMatcher("/login/mail/confirm"),
                        new AntPathRequestMatcher("/member/findId"),
                        new AntPathRequestMatcher("/member/id/find"),
                        new AntPathRequestMatcher("/member/password/find"),
                        new AntPathRequestMatcher("/member/searchPw/change"),
                        new AntPathRequestMatcher("/member/sign-in"),
                        new AntPathRequestMatcher("/emailCheck.js"),
                        new AntPathRequestMatcher("/js/jquery-3.6.0.min.js")
                ).permitAll()
                // USER 권한이 있어야 요청할 수 있음
                .requestMatchers(new AntPathRequestMatcher("/**")).hasRole("USER")
                // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                .anyRequest().authenticated()
                .and()
                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, memberJpaRepository), UsernamePasswordAuthenticationFilter.class);
//    http.cors()
//        .disable() // cors 방지
//        .csrf()
//        .disable() // csrf 방지
//        .formLogin()
//        .disable() // 기본 로그인페이지 없애기
//        .headers()
//        .frameOptions()
//        .disable();
    http.logout()
        .logoutUrl("/logout") // 로그아웃 처리 URL (= form action url)
        // .logoutSuccessUrl("/login") // 로그아웃 성공 후 targetUrl,
        // logoutSuccessHandler 가 있다면 효과 없으므로 주석처리.
        .addLogoutHandler(
            (request, response, authentication) -> {
              // 사실 굳이 내가 세션 무효화하지 않아도 됨.
              // LogoutFilter가 내부적으로 해줌.
              HttpSession session = request.getSession();
              if (session != null) {
                session.invalidate();
              }
            }) // 로그아웃 핸들러 추가
        .logoutSuccessHandler(
            (request, response, authentication) -> {
              response.sendRedirect("/");
            }) // 로그아웃 성공 핸들러
        .deleteCookies("boardView") // 로그아웃 후 삭제할 쿠키 지정
        .deleteCookies("swsToken"); // 로그아웃 후 삭제할 쿠키 지정

        return http.build();
    }
}
