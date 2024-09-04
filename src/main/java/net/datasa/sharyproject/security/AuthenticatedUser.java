/**
 * 
 */
package net.datasa.sharyproject.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//사용자 인증 정보 객체
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthenticatedUser implements UserDetails {

	private static final long serialVersionUID = 8230398800837763822L; 

	String memberId;
	String memberPw;
	String nickname;
	String roleName;
	boolean enabled;//사용가능한 아이디인지 확인하는 것
	
	//권한명 리턴
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return Collections.singletonList(new SimpleGrantedAuthority(roleName));
	}

	//is로 시작하는 4개 모두 트루가 나와야 실행이 됨
	@Override
	public boolean isAccountNonExpired() { //로그인할 떄 여기서 펄스가 나오면 리턴이 안됨
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	//정상 작동여부
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	//비밀번호 리턴
	@Override
	public String getPassword() { //시큐리티가 겟 여기를 호출해서 비밀번호가 맞는지 맞춰봄.
		return memberPw;
	}

	//사용자 아이디 리턴
	@Override
	public String getUsername() {
		return memberId;
	}



}
