package sws.NoticeBoard.repository;

import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sws.NoticeBoard.domain.Member;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
  private final EntityManager em;

  public void save(Member member) {
    em.persist(member);
  }

  public Member findByLoginId(String loginId) {
    return em.createQuery("select m from Member m where m.loginId = :loginId", Member.class)
        .setParameter("loginId", loginId)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  public Member findByEmail(String email) {
    return em.createQuery("select m from Member m where m.email = :email", Member.class)
        .setParameter("email", email)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  public Member findByNameAndEmail(String realName, String email) {
    return em.createQuery(
            "select m from Member m where m.realName = :realName and m.email = :email",
            Member.class)
        .setParameter("realName", realName)
        .setParameter("email", email)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  public List<Member> findAll() {
    return em.createQuery("select m from Member m", Member.class).getResultList();
  }
}
