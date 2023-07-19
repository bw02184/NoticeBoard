package sws.NoticeBoard;

import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors()
        .disable() // cors 방지
        .csrf()
        .disable() // csrf 방지
        .formLogin()
        .disable() // 기본 로그인페이지 없애기
        .headers()
        .frameOptions()
        .disable();
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
        .deleteCookies("boardView"); // 로그아웃 후 삭제할 쿠키 지정

    return http.build();
  }
}
