package sws.NoticeBoard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sws.NoticeBoard.domain.Member;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
}
