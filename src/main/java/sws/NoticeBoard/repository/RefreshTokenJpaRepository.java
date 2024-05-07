package sws.NoticeBoard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sws.NoticeBoard.domain.RefreshToken;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByMemberId(Long memberId);

}
