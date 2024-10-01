package net.datasa.sharyproject.repository.personal;

import net.datasa.sharyproject.domain.entity.personal.PersonalLikeEntity;
import net.datasa.sharyproject.domain.entity.personal.PersonalNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PersonalLikeRepository extends JpaRepository<PersonalLikeEntity, Long> {
    // 노트 번호와 사용자 ID를 통해 좋아요 상태를 조회
    Optional<PersonalLikeEntity> findByPersonalNoteNumAndMemberId(Integer personalNoteNum, String memberId);

    // 특정 노트에 좋아요가 눌린 수를 계산
    int countByPersonalNoteNumAndLikeClicked(Integer personalNoteNum, boolean likeClicked);

    // 좋아요가 클릭된 노트만 조회 (PersonalLikeEntity와 PersonalNoteEntity를 조인)
    @Query("SELECT n.noteTitle AS noteTitle, COUNT(l) AS likeCount FROM PersonalLikeEntity l JOIN l.personalNote n WHERE l.likeClicked = true GROUP BY n.personalNoteNum, n.noteTitle ORDER BY likeCount DESC")
    List<Object[]> findTopLikedNotes();
}