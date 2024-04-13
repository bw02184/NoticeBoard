package sws.NoticeBoard.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sws.NoticeBoard.controller.form.MemberDeleteForm;
import sws.NoticeBoard.controller.form.MemberIdFindForm;
import sws.NoticeBoard.controller.form.MemberPwFindForm;
import sws.NoticeBoard.controller.form.MemberPwUpdateForm;
import sws.NoticeBoard.controller.form.MemberSearchPwChangeForm;
import sws.NoticeBoard.domain.Board;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.jwt.JwtToken;
import sws.NoticeBoard.jwt.JwtTokenProvider;
import sws.NoticeBoard.repository.BoardRepository;
import sws.NoticeBoard.repository.CommentRepository;
import sws.NoticeBoard.repository.MemberJpaRepository;
import sws.NoticeBoard.repository.MemberRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  private final BoardRepository boardRepository;
  private final CommentRepository commentRepository;
  private final PasswordEncoder passwordEncoder;



  public Member MemberSearch(String loginId) {
    return memberRepository.findByLoginId(loginId);
  }

  public void memberInfoUpdate(
      String loginId, String realName, String email, String checkedEmail, Boolean emailConfirm) {
    if (!email.equals(checkedEmail) || !emailConfirm) {
      throw new IllegalStateException("이메일 본인인증을 해주세요");
    }
    Member emailMember = memberRepository.findByEmail(email);
    Member findMember = memberRepository.findByLoginId(loginId);
    // 이메일 변경을 하지 않을 경우 validation 하지 않기
    if (emailMember != null && !findMember.getEmail().equals(email)) {
      throw new IllegalStateException("이메일로 가입한 회원이 존재합니다. 다른 이메일을 적어 주세요");
    }
    findMember.setRealName(realName);
    findMember.setEmail(email);
  }

  public void memberPWUpdate(MemberPwUpdateForm form) {
    Member findMember = memberRepository.findByLoginId(form.getLoginId());
    if (!passwordEncoder.matches(form.getPassword(), findMember.getPassword())) {
      throw new IllegalStateException("현재 비밀번호를 다시 입력해 주세요");
    }
    if (!form.getNewPassword().equals(form.getNewPassword2())) {
      throw new IllegalStateException("변경할 비밀번호를 다시 입력해 주세요");
    }
    findMember.setPassword(passwordEncoder.encode(form.getNewPassword()));
  }

  public String memberIdFind(MemberIdFindForm form) {
    if (!form.getEmail().equals(form.getCheckedEmail()) || !form.getEmailConfirm()) {
      throw new IllegalStateException("이메일 본인인증을 해주세요");
    }
    Member findMember = memberRepository.findByNameAndEmail(form.getRealName(), form.getEmail());
    if (findMember == null) {
      throw new IllegalStateException("이름 또는 이메일을 다시 입력해 주세요");
    }
    return findMember.getLoginId();
  }

  public Member memberPwFind(MemberPwFindForm form) {
    if (!form.getEmail().equals(form.getCheckedEmail()) || !form.getEmailConfirm()) {
      throw new IllegalStateException("이메일 본인인증을 해주세요");
    }
    Member findMember = memberRepository.findByLoginId(form.getLoginId());
    if (findMember == null) {
      throw new IllegalStateException("아이디를 다시 입력해 주세요");
    }
    if (!findMember.getEmail().equals(form.getEmail())
        || !findMember.getRealName().equals(form.getRealName())) {
      throw new IllegalStateException("입력한 아이디와 이름, 이메일 정보가 틀립니다. 정확하게 입력해 주세요");
    }
    return findMember;
  }

  public void memberSearchPwChange(MemberSearchPwChangeForm form) {
    Member findMember = memberRepository.findByLoginId(form.getLoginId());
    if (findMember == null) {
      throw new RuntimeException("비밀번호 변경을 다시 해 주세요");
    }
    if (!form.getNewPassword().equals(form.getNewPassword2())) {
      throw new IllegalStateException("변경할 비밀번호를 다시 입력해 주세요");
    }
    findMember.setPassword(passwordEncoder.encode(form.getNewPassword()));
  }

  public void memberDelete(MemberDeleteForm form) {
    Member findMember = memberRepository.findByLoginId(form.getLoginId());
    if (!passwordEncoder.matches(form.getPassword(), findMember.getPassword())) {
      throw new IllegalStateException("현재 비밀번호를 다시 입력해 주세요");
    }
    Member deletedMember = memberRepository.findByLoginId("deleted");
    // 회원 탈퇴 시 그동안 쓴 게시물을 탈퇴회원이 쓴 게시물로 변경(탈퇴처리)
    List<Board> boardList = boardRepository.findByBoardLoginId(form.getLoginId());
    for (Board board : boardList) {
      board.setMember(deletedMember);
    }
    commentRepository.deleteByMemberLoginId(findMember.getLoginId());
    memberRepository.delete(findMember);
  }
  public Member memberLoad(String loginId){
    return memberRepository.findByLoginId(loginId);
  }

}
