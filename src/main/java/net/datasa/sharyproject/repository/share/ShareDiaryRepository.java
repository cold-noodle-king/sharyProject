package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.personal.PersonalDiaryEntity;
import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareDiaryRepository extends JpaRepository<ShareDiaryEntity, Integer> {
    /**
     * 특정 회원의 모든 다이어리를 조회합니다.
     * @param memberId 회원 ID
     * @return 회원의 개인 다이어리 리스트
     */
    List<ShareDiaryEntity> findByMember_MemberId(String memberId);
}
