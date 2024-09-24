package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.share.ShareLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareLikeRepository extends JpaRepository<ShareLikeEntity, Integer> {

}
