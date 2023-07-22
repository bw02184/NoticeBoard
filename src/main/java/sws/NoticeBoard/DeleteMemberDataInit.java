package sws.NoticeBoard;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class DeleteMemberDataInit {
  private final InitService initService;

  @PostConstruct
  public void init() {
    initService.dbInit1();
  }
  /** 탈퇴회원 전용 데이터 추가 */
  @Component
  @Transactional
  @RequiredArgsConstructor
  static class InitService {
    private final EntityManager em;
    private final MemberRepository memberRepository;

    public void dbInit1() {
      Member findMember = memberRepository.findByLoginId("deleted");
      if (findMember != null) {
        return;
      }
      Member member = new Member();
      member.setRealName("탈퇴회원");
      member.setLoginId("deleted");
      member.setPassword("deleted"); // 일부러 passwordEncoder 미사용(로그인 되지 않도록 하기 위해)
      member.setEmail("delete@delete.sws");
      member.setIsAdmin(false);
      em.persist(member);
    }
  }
}
