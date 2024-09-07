package net.datasa.sharyproject.repository.member;

import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
    Optional<Object> findByNickname(String searchNick);
    // 모든 회원을 조회하기 위한 findAll 메서드는 JpaRepository에 의해 자동으로 제공됩니다.
}
