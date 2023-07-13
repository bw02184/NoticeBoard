package sws.NoticeBoard.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.repository.MemberRepository;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    public Member login(String loginId, String password){
        Member findmember = memberRepository.findByLoginId(loginId);
        if(findmember == null) return null;
        else if(findmember.getPassword().equals(password))
            return findmember;
        return null;
    }

}
