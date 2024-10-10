package net.datasa.sharyproject.repository.follow;

import net.datasa.sharyproject.domain.entity.follow.FollowEntity;
import net.datasa.sharyproject.domain.entity.follow.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * FollowRepository 인터페이스
 * 이 인터페이스는 데이터베이스에서 팔로우(FollowEntity) 관계 데이터를 관리하기 위해 사용됩니다.
 * JpaRepository를 상속받아 기본적인 CRUD(Create, Read, Update, Delete) 기능을 자동으로 제공받습니다.
 * 팔로우 및 팔로워 목록을 조회하는 커스텀 메서드가 정의되어 있습니다.
 */
public interface FollowRepository extends JpaRepository<FollowEntity, FollowId> {

    /**
     * 특정 사용자를 팔로우한 사용자 목록을 조회하는 메서드
     * @param currentUserId 팔로우 받은 사용자의 ID
     * @return 해당 사용자를 팔로우한 사용자의 목록 (FollowEntity 리스트로 반환)
     */
    List<FollowEntity> findByFollowingId(String currentUserId);

    /**
     * 특정 사용자가 팔로우하고 있는 사용자 목록을 조회하는 메서드
     * @param currentUserId 팔로우 한 사용자의 ID
     * @return 해당 사용자가 팔로우하고 있는 사용자의 목록 (FollowEntity 리스트로 반환)
     */
    List<FollowEntity> findByFollowerId(String currentUserId);
}
