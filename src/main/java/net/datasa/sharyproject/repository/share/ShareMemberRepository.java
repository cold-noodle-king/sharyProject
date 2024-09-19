package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
import net.datasa.sharyproject.domain.entity.share.ShareMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShareMemberRepository extends JpaRepository<ShareMemberEntity, Integer> {
    // 공유 다이어리와 상태로 공유 멤버를 조회하는 메서드
    List<ShareMemberEntity> findByShareDiaryAndStatus(ShareDiaryEntity shareDiary, String status);

    //공유 다이어리 번호로 조회하고 싶다면
    List<ShareMemberEntity> findByShareDiary_ShareDiaryNumAndStatus(Integer shareDiaryNum, String status);

    //공유 다이어리와 멤버 아이디로 가입 요청한 회원을 조회
    ShareMemberEntity ShareDiary_shareDiaryNumAndMember_memberId(Integer diaryNum, String memberId);
    
    //이미 가입한 회원을 조회하는 메서드
    Optional<ShareMemberEntity> findByShareDiary_ShareDiaryNumAndMember_MemberId(Integer shareDiaryNum, String memberId);

}
