package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.personal.PersonalDiaryEntity;
import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShareDiaryRepository extends JpaRepository<ShareDiaryEntity, Integer> {
    /**
     * 특정 회원의 모든 다이어리를 조회
     * @param memberId 회원 ID
     * @return 회원의 개인 다이어리 리스트
     */
    List<ShareDiaryEntity> findByMember_MemberId(String memberId);
    @Query("SELECT d FROM ShareDiaryEntity d LEFT JOIN FETCH d.shareMemberList WHERE d.shareDiaryNum = :diaryNum")
    Optional<ShareDiaryEntity> findByIdWithMembers(@Param("diaryNum") Integer diaryNum);



}
