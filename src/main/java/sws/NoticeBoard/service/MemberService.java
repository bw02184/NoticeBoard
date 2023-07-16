package sws.NoticeBoard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sws.NoticeBoard.controller.form.MemberIdFindForm;
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

  public void memberInfoUpdate(
      String loginId, String realName, String email, Boolean emailConfirm) {
    if (!emailConfirm) {
      throw new IllegalStateException("이메일 본인인증을 해주세요");
    }
    Member emailMember = memberRepository.findByEmail(email);
    if (emailMember != null) {
      throw new IllegalStateException("이메일로 가입한 회원이 존재합니다. 다른 이메일을 적어 주세요");
    }
    Member findMember = memberRepository.findByLoginId(loginId);
    findMember.setRealName(realName);
    findMember.setEmail(email);
  }

  public void memberPWUpdate(MemberPWUpdateForm form) {
    Member findMember = memberRepository.findByLoginId(form.getLoginId());
    if (!passwordEncoder.matches(form.getPassword(), findMember.getPassword())) {
      throw new IllegalStateException("현재 비밀번호를 다시 입력해 주세요");
    }
    if (!form.getNewPassword().equals(form.getNewPassword2())) {
      throw new IllegalStateException("재확인 비밀번호를 다시 입력해 주세요");
    }
    findMember.setPassword(passwordEncoder.encode(form.getNewPassword()));
  }

  public String memberIdFind(MemberIdFindForm form) {
    Member emailMember = memberRepository.findByEmail(form.getEmail());
    if (!form.getEmailConfirm()) {
      throw new IllegalStateException("이메일 본인인증을 해주세요");
    }
    Member findMember = memberRepository.findByNameAndEmail(form.getRealName(), form.getEmail());
    return findMember.getLoginId();
  }
}
