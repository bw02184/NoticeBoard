package sws.NoticeBoard.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sws.NoticeBoard.domain.RefreshToken;
import sws.NoticeBoard.repository.RefreshTokenJpaRepository;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
	private final RefreshTokenJpaRepository refreshTokenJpaRepository;

	public Optional<RefreshToken> refreshTokenLoad(Long memberId) {
		return refreshTokenJpaRepository.findByMemberId(memberId);
	}

	public void save(RefreshToken rf) {
		Optional<RefreshToken> prevRf = refreshTokenJpaRepository.findByMemberId(rf.getMember().getId());
		long count = prevRf.stream().count();

		if (count > 0) {
			prevRf.orElse(null).setExpiredAt(rf.getExpiredAt());
			prevRf.orElse(null).setToken(rf.getToken());
		} else
			refreshTokenJpaRepository.save(rf);
	}

}
