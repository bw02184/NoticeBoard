package sws.NoticeBoard.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.repository.MemberJpaRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberJpaRepository memberJpaRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return memberJpaRepository.findByLoginId(username)
			.map(this::createUserDetails)
			.orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
	}

	// 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
	private UserDetails createUserDetails(Member member) {
		return User.builder()
			.username(member.getUsername())
			.password((member.getPassword()))
			.roles(member.getRoles().toArray(new String[0]))
			.build();
	}

}
