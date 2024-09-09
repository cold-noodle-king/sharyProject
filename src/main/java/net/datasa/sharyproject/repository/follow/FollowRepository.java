package net.datasa.sharyproject.repository.follow;

import net.datasa.sharyproject.domain.entity.follow.FollowEntity;
import net.datasa.sharyproject.domain.entity.follow.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 게시판 repository
 */

//FollowId는 복합키 정의 클래스
public interface FollowRepository extends JpaRepository<FollowEntity, FollowId> {

    List<FollowEntity> findByFollowingId(String currentUserId);

    List<FollowEntity> findByFollowerId(String currentUserId);
}
