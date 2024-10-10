package net.datasa.sharyproject.domain.entity.follow;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * FollowId 클래스
 * 이 클래스는 FollowEntity에서 복합 키를 정의하는 데 사용됩니다.
 * 팔로워(follower)와 팔로잉(following)의 두 개의 필드를 합쳐서 하나의 복합 키를 생성합니다.
 */
@Data  // Lombok 어노테이션으로 getter, setter, toString, equals, hashCode 메서드를 자동으로 생성
@NoArgsConstructor  // 기본 생성자를 자동으로 생성하는 Lombok 어노테이션
@AllArgsConstructor  // 모든 필드를 포함한 생성자를 자동으로 생성하는 Lombok 어노테이션
public class FollowId implements Serializable {

    // 팔로우를 하는 사람의 ID (복합 키의 일부)
    private String followerId;

    // 팔로우를 받는 사람의 ID (복합 키의 일부)
    private String followingId;

}

