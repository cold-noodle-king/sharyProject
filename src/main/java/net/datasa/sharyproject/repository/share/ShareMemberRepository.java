package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.share.ShareMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareMemberRepository extends JpaRepository<ShareMemberEntity, Integer> {
}
