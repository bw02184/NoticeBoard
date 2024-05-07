package sws.NoticeBoard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sws.NoticeBoard.domain.Member;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByLoginId(String loginId);
}
