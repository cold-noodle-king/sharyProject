package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.share.ShareNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareNoteRepository extends JpaRepository<ShareNoteEntity, Integer> {

    List<ShareNoteEntity> findByShareDiary_ShareDiaryNum(Integer diaryNum);
}
