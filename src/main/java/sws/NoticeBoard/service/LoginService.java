package sws.NoticeBoard.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sws.NoticeBoard.controller.form.MemberSaveForm;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.repository.MemberRepository;

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

  public void join(MemberSaveForm form) {
    Member findMember = memberRepository.findByLoginId(form.getLoginId());
    if (findMember != null) {
      throw new IllegalStateException("중복된 아이디 입니다.");
    }
    if (!form.getPassword().equals(form.getPassword2())) {
      throw new IllegalStateException("비밀번호를 다시 입력해 주세요");
    }
    if (!form.getEmailConfirm()) {
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
    member.setCreatedAt(LocalDateTime.now());
    member.setIsAdmin(false);

    memberRepository.save(member);
  }
}
