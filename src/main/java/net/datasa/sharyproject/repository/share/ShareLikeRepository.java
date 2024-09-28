package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.share.ShareLikeEntity;
import net.datasa.sharyproject.domain.entity.share.ShareNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareLikeRepository extends JpaRepository<ShareLikeEntity, Integer> {
        // 노트 넘버와 멤버 아이디로 shareLike엔티티를 추출하는 메서드
        ShareLikeEntity findByShareNoteAndMember(ShareNoteEntity shareNote, MemberEntity member);

        // 감정별 좋아요 개수 카운트
        @Query("SELECT sl.emotionName, COUNT(sl) FROM ShareLikeEntity sl WHERE sl.shareNote.shareNoteNum = :noteNum GROUP BY sl.emotionName")
        List<Object[]> countByEmotionName(@Param("noteNum") Integer noteNum);

}
