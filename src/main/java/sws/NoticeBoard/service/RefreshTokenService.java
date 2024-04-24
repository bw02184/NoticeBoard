package sws.NoticeBoard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.domain.RefreshToken;
import sws.NoticeBoard.repository.RefreshTokenJpaRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public Optional<RefreshToken> refreshTokenLoad(Long memberId){
        return refreshTokenJpaRepository.findByMemberId(memberId);
    }
    public void save(RefreshToken rf){
        Optional<RefreshToken> prevRf= refreshTokenJpaRepository.findByMemberId(rf.getMember().getId());
        long count = prevRf.stream().count();

        if(count > 0){
            prevRf.orElse(null).setExpiredAt(rf.getExpiredAt());
            prevRf.orElse(null).setToken(rf.getToken());
        } else
            refreshTokenJpaRepository.save(rf);
    }

}
