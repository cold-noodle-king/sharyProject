package net.datasa.sharyproject.repository.member;

import net.datasa.sharyproject.domain.entity.CategoryEntity;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.member.UserCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCategoryRepository extends JpaRepository<UserCategoryEntity, Integer> {
    // 특정 멤버에 해당하는 유저 카테고리 모두 삭제
    void deleteByMember(MemberEntity member);
    // 특정 회원과 카테고리에 해당하는 레코드가 있는지 확인
    boolean existsByMemberAndCategory(MemberEntity member, CategoryEntity category);
}
