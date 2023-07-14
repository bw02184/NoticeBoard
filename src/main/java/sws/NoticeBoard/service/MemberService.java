package sws.NoticeBoard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.repository.MemberRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;

  public void MemberInfoUpdate(String loginId, String realName, String email) {
    Member findMember = memberRepository.findByLoginId(loginId);
    findMember.setRealName(realName);
    findMember.setEmail(email);
  }

  public Member MemberSearch(String loginId) {
    return memberRepository.findByLoginId(loginId);
  }
}
