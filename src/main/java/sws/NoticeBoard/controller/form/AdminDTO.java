package sws.NoticeBoard.controller.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;

@Data
public class AdminDTO {
	private Long id;
	private String loginId;
	private String password;
	private List<String> role = new ArrayList<>();
	private String token;
	Collection<? extends GrantedAuthority> authorities;
}
