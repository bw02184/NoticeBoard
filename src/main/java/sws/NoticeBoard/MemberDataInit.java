package sws.NoticeBoard;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sws.NoticeBoard.domain.Grade;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class MemberDataInit {
  private final InitService initService;

  @PostConstruct
  public void init() {
    initService.dbInit1();
    initService.dbInit2();
    initService.dbInit3();
    initService.dbInit4();
  }

  /** 전용 데이터 추가 */
  @Component
  @Transactional
  @RequiredArgsConstructor
  static class InitService {
    private final EntityManager em;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void dbInit1() {
      Member findMember = memberRepository.findByLoginId("deleted");
      if (findMember != null) {
        return;
      }
      Member member = new Member();
      member.setRealName("탈퇴회원");
      member.setLoginId("deleted");
      member.setPassword(passwordEncoder.encode("deleted"));
      member.setEmail("delete@delete.sws");
      member.setGrade(Grade.NORMAL);
      em.persist(member);
    }

    public void dbInit2() {
      Member findMember1 = memberRepository.findByLoginId("test");
      if (findMember1 != null) {
        return;
      }
      Member member1 = new Member();
      member1.setRealName("sws");
      member1.setLoginId("test");
      member1.setPassword(passwordEncoder.encode("test"));
      member1.setEmail("sws@sws.sws");
      member1.setGrade(Grade.NORMAL);
      em.persist(member1);
    }

    public void dbInit3() {
      Member findMember2 = memberRepository.findByLoginId("test2");
      if (findMember2 != null) {
        return;
      }
      Member member2 = new Member();
      member2.setRealName("sws2");
      member2.setLoginId("test2");
      member2.setPassword(passwordEncoder.encode("test2"));
      member2.setEmail("sws2@sws.sws");
      member2.setGrade(Grade.NORMAL);
      em.persist(member2);
    }

    public void dbInit4() {
      Member findMember2 = memberRepository.findByLoginId("admin");
      if (findMember2 != null) {
        return;
      }
      Member member2 = new Member();
      member2.setRealName("root");
      member2.setLoginId("admin");
      member2.setPassword(passwordEncoder.encode("admin"));
      member2.setEmail("root@sws.sws");
      member2.setGrade(Grade.ADMIN);
      em.persist(member2);
    }
  }
}
