package net.datasa.sharyproject.security;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.repository.member.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticatedUserDetailsService implements UserDetailsService {

	private final BCryptPasswordEncoder passwordEncoder; //passwordEncoder 변수명임
	private final MemberRepository memberRepository;

	@Override		 //load가 DB에서 유저네임 기준으로 가져옴
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		MemberEntity memberEntity = memberRepository.findById(username)
				.orElseThrow(()->new EntityNotFoundException("아이디가 없습니다."));

		log.debug("조회정보 : {}", memberEntity);
		log.debug("isEnabled : {}", memberEntity.getEnabled());

		AuthenticatedUser user = AuthenticatedUser.builder()
				.memberId(memberEntity.getMemberId())
				.memberPw(memberEntity.getMemberPw())
				.nickname(memberEntity.getNickname()) //선택적 필드
				.roleName(memberEntity.getRoleName())// 권한 설정
				.enabled(memberEntity.getEnabled())
				.build();
		log.debug("인증정보 : {}", user);

		return user; //Spring Security에서 사용될 UserDetails 객체 반환
	}

}

