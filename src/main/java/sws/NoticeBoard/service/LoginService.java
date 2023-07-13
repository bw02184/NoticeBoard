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
    if (passwordEncoder.matches(password, findMember.getPassword())) return findMember;
    return null;
  }

  public void join(MemberSaveForm form) {
    Member findMember = memberRepository.findByLoginId(form.getLoginId());
    // TODO: 2023-07-13  에러 처리 완료해야 함 (현재 화이트 페이지 발생)
    if (findMember != null) {
      throw new IllegalStateException("중복된 아이디 입니다.");
    }

    if (!form.getPassword().equals(form.getPassword2())) {
      throw new IllegalStateException("비밀번호를 다시 입력 해 주세요");
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
