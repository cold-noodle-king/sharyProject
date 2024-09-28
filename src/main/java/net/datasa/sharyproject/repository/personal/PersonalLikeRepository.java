package net.datasa.sharyproject.repository.personal;

import net.datasa.sharyproject.domain.entity.personal.PersonalLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalLikeRepository extends JpaRepository<PersonalLikeEntity, Long> {
    // 노트 번호와 사용자 ID를 통해 좋아요 상태를 조회
    Optional<PersonalLikeEntity> findByPersonalNoteNumAndMemberId(Integer personalNoteNum, String memberId);

    // 특정 노트에 좋아요가 눌린 수를 계산
    int countByPersonalNoteNumAndLikeClicked(Integer personalNoteNum, boolean likeClicked);
}