package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
import net.datasa.sharyproject.domain.entity.share.ShareMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareMemberRepository extends JpaRepository<ShareMemberEntity, Integer> {
    // 공유 다이어리와 상태로 공유 멤버를 조회하는 메서드
    List<ShareMemberEntity> findByShareDiaryAndStatus(ShareDiaryEntity shareDiary, String status);

    // 또는 공유 다이어리 번호로 조회하고 싶다면
    List<ShareMemberEntity> findByShareDiary_ShareDiaryNumAndStatus(Integer shareDiaryNum, String status);
}
