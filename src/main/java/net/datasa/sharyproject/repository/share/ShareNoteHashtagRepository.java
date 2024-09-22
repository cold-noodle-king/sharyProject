package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.share.ShareNoteHashtagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareNoteHashtagRepository extends JpaRepository<ShareNoteHashtagEntity, Long> {

    List<ShareNoteHashtagEntity> findByShareNote_ShareNoteNum(Integer noteNum);
}
