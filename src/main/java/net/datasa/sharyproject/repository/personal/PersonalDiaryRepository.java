package net.datasa.sharyproject.repository.personal;

import net.datasa.sharyproject.domain.entity.personal.PersonalDiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PersonalDiaryEntity 엔티티를 위한 레포지토리 인터페이스
 * 데이터베이스 연동을 위한 JPA Repository입니다.
 */
@Repository
public interface PersonalDiaryRepository extends JpaRepository<PersonalDiaryEntity, Integer> {

    /**
     * 특정 회원의 모든 다이어리를 조회합니다.
     * @param memberId 회원 ID
     * @return 회원의 개인 다이어리 리스트
     */
    List<PersonalDiaryEntity> findByMember_MemberIdOrderByCreatedDateDesc(String memberId);

    /**
     * 특정 카테고리 번호에 해당하는 모든 다이어리를 조회합니다.
     * @param categoryNum 카테고리 번호
     * @return 해당 카테고리의 개인 다이어리 리스트
     */
    @Query("SELECT pd FROM PersonalDiaryEntity pd WHERE pd.category.categoryNum = :categoryNum")
    List<PersonalDiaryEntity> findByCategoryNum(@Param("categoryNum") Integer categoryNum);

    /**
     * 특정 커버 번호에 해당하는 모든 다이어리를 조회합니다.
     * @param coverNum 커버 번호
     * @return 해당 커버를 사용하는 개인 다이어리 리스트
     */
    @Query("SELECT pd FROM PersonalDiaryEntity pd WHERE pd.coverTemplate.coverNum = :coverNum")
    List<PersonalDiaryEntity> findByCoverNum(@Param("coverNum") Integer coverNum);

    /**
     * 회원 ID와 카테고리 번호로 다이어리를 조회합니다.
     * @param memberId 회원 ID
     * @param categoryNum 카테고리 번호
     * @return 회원의 특정 카테고리 다이어리 리스트
     */
    @Query("SELECT pd FROM PersonalDiaryEntity pd WHERE pd.member.memberId = :memberId AND pd.category.categoryNum = :categoryNum")
    List<PersonalDiaryEntity> findByMemberAndCategory(@Param("memberId") String memberId, @Param("categoryNum") Integer categoryNum);
}