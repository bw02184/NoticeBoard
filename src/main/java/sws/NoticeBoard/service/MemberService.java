package sws.NoticeBoard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sws.NoticeBoard.controller.form.MemberPWUpdateForm;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.repository.MemberRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public Member MemberSearch(String loginId) {
    return memberRepository.findByLoginId(loginId);
  }

  public void MemberInfoUpdate(String loginId, String realName, String email) {
    Member findMember = memberRepository.findByLoginId(loginId);
    findMember.setRealName(realName);
    findMember.setEmail(email);
  }

  public void MemberPWUpdate(MemberPWUpdateForm form) {
    Member findMember = memberRepository.findByLoginId(form.getLoginId());
    if (!passwordEncoder.matches(form.getPassword(), findMember.getPassword())) {
      throw new IllegalStateException("현재 비밀번호를 다시 입력해 주세요");
    }
    if (!form.getNewPassword().equals(form.getNewPassword2())) {
      throw new IllegalStateException("재확인 비밀번호를 다시 입력해 주세요");
    }
    findMember.setPassword(passwordEncoder.encode(form.getNewPassword()));
  }
}
