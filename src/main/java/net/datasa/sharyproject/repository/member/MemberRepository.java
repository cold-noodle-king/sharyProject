package net.datasa.sharyproject.repository.member;

import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
    Optional<MemberEntity> findByNickname(String searchNick);
}
