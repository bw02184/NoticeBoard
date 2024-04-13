package sws.NoticeBoard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sws.NoticeBoard.controller.form.MemberSaveForm;
import sws.NoticeBoard.controller.form.SignInDto;
import sws.NoticeBoard.domain.Grade;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.jwt.JwtToken;
import sws.NoticeBoard.jwt.JwtTokenProvider;
import sws.NoticeBoard.repository.MemberRepository;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class LoginService {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public Member login(String loginId, String password) {
    Member findMember = memberRepository.findByLoginId(loginId);
    if (findMember == null) return null;
    if (passwordEncoder.matches(password, findMember.getPassword()))
      return findMember; // matches(raw, encoded)
    return null;
  }

  public Member join(MemberSaveForm form) throws UnsupportedEncodingException {
    Member findMember = memberRepository.findByLoginId(form.getLoginId());
    if (findMember != null) {
      throw new IllegalStateException("중복된 아이디 입니다.");
    }
    if (!form.getPassword().equals(form.getPassword2())) {
      throw new IllegalStateException("비밀번호를 다시 입력해 주세요");
    }
    if (!form.getEmail().equals(form.getCheckedEmail()) || !form.getEmailConfirm()) {
      throw new IllegalStateException("이메일 본인인증을 해주세요");
    }
    Member emailMember = memberRepository.findByEmail(form.getEmail());
    if (emailMember != null) {
      throw new IllegalStateException("이메일로 가입한 회원이 존재합니다. 다른 이메일을 적어 주세요");
    }
    Member member = new Member();
    member.setLoginId(form.getLoginId());
    member.setEmail(form.getEmail());
    member.setPassword(passwordEncoder.encode(form.getPassword()));
    member.setRealName(form.getRealName());
    member.setGrade(Grade.NORMAL);
    List<String> roles = new ArrayList<>();
    roles.add("USER");
    member.setRoles(roles);

    memberRepository.save(member);
    return member;

  }

//  @Transactional
//  public JwtToken signIn(String username, String password) {
//    // 1. username + password 를 기반으로 Authentication 객체 생성
//    // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
//    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
//
//    // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
//    // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
//    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//
//    // 3. 인증 정보를 기반으로 JWT 토큰 생성
//    JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
//
//    return jwtToken;
//  }
}
