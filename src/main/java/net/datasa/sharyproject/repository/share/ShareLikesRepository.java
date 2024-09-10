package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.share.ShareLikeEntity;
import net.datasa.sharyproject.domain.entity.share.ShareLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareLikesRepository extends JpaRepository<ShareLikeEntity, ShareLikes> {
}
