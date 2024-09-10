package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareDiaryRepository extends JpaRepository<ShareDiaryEntity, Integer> {
}
