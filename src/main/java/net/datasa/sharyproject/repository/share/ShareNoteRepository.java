package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.share.ShareNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareNoteRepository extends JpaRepository<ShareNoteEntity, Integer> {

}
