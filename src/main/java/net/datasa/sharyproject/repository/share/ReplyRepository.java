package net.datasa.sharyproject.repository.share;

import net.datasa.sharyproject.domain.entity.share.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {
}
