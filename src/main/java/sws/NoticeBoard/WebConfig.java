package sws.NoticeBoard;

import hello.login.web.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sws.NoticeBoard.interceptor.LoginCheckInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new LogInterceptor())
        .order(1)
        .addPathPatterns("/**")
        .excludePathPatterns("/css/**", "/*.ico", "/error");

    registry
        .addInterceptor(new LoginCheckInterceptor())
        .order(2)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/",
            "/login",
            "/login/new",
            "/login/mail/confirm",
            "/member/id/find",
            "/member/findId",
            "/member/password/find",
            "/member/searchPw/change",
            "/logout",
            "/css/**",
            "/*ico",
            "/emailCheck.js",
            "/error");
  }
}
