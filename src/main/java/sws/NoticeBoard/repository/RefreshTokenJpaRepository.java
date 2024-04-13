package sws.NoticeBoard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMemberId(Long memberId);

}
