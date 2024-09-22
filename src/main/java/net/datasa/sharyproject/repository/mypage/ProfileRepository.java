package net.datasa.sharyproject.repository.mypage;

import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {

    // 기존 멤버 엔티티를 통해 프로필을 조회하는 메서드
    Optional<ProfileEntity> findByMember(MemberEntity member);

    // 추가된 코드: memberId를 통해 프로필을 조회하는 메서드
    Optional<ProfileEntity> findByMember_MemberId(String memberId);
}