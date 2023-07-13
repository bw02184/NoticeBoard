package sws.NoticeBoard.repository;

import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sws.NoticeBoard.domain.Member;
import java.util.List;
@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;
    public void save(Member member) {
        em.persist(member);
    }

    public Member findByLoginId(String loginId){
        return em.createQuery("select m from Member m where m.loginId = :loginId", Member.class)
            .setParameter("loginId", loginId)
            .getResultStream()
            .findFirst()
            .orElse(null);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}
