package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
import net.datasa.sharyproject.domain.entity.share.ShareMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShareMemberRepository extends JpaRepository<ShareMemberEntity, Integer> {

    // 공유 다이어리와 상태로 공유 멤버를 조회하는 메서드
    List<ShareMemberEntity> findByShareDiaryAndStatus(ShareDiaryEntity shareDiary, String status);

    // 공유 다이어리 번호로 상태별 조회
    List<ShareMemberEntity> findByShareDiary_ShareDiaryNumAndStatus(Integer shareDiaryNum, String status);

    // 공유 다이어리 번호와 멤버 아이디로 공유 멤버를 조회
    Optional<ShareMemberEntity> findByShareDiary_ShareDiaryNumAndMember_MemberId(Integer diaryNum, String memberId);

    // 특정 memberId에 해당하며, status가 ACCEPTED인 ShareDiaryEntity 목록을 가져오는 쿼리
    @Query("SELECT sm.shareDiary FROM ShareMemberEntity sm WHERE sm.member.memberId = :memberId AND sm.status = 'ACCEPTED'")
    List<ShareDiaryEntity> findAcceptedShareDiariesByMemberId(String memberId);

    // 특정 memberId와 shareDiaryNum에 해당하며, status가 ACCEPTED인 ShareDiaryEntity를 가져오는 쿼리
    @Query("SELECT sm.shareDiary FROM ShareMemberEntity sm WHERE sm.shareDiary.shareDiaryNum = :diaryNum AND sm.member.memberId = :memberId AND sm.status = 'ACCEPTED'")
    Optional<ShareDiaryEntity> findAcceptedShareDiaryByDiaryNumAndMemberId(Integer diaryNum, String memberId);

    // 특정 memberId와 shareDiaryNum에 해당하며, status가 PENDING인 ShareDiaryEntity를 가져오는 쿼리
    @Query("SELECT sm.shareDiary FROM ShareMemberEntity sm WHERE sm.shareDiary.shareDiaryNum = :diaryNum AND sm.member.memberId = :memberId AND sm.status = 'PENDING'")
    Optional<ShareDiaryEntity> findPendingShareDiaryByDiaryNumAndMemberId(Integer diaryNum, String memberId);

    // 특정 memberId와 shareDiaryNum에 해당하며, status가 REJECTED인 ShareDiaryEntity를 가져오는 쿼리
    @Query("SELECT sm.shareDiary FROM ShareMemberEntity sm WHERE sm.shareDiary.shareDiaryNum = :diaryNum AND sm.member.memberId = :memberId AND sm.status = 'REJECTED'")
    Optional<ShareDiaryEntity> findRejectedShareDiaryByDiaryNumAndMemberId(Integer diaryNum, String memberId);

    // 상태가 ACCEPTED인 멤버 수를 세는 메서드
    int countByShareDiary_ShareDiaryNumAndStatus(Integer shareDiaryNum, String status);

}


