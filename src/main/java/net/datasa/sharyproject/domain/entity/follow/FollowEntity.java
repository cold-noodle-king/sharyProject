package net.datasa.sharyproject.domain.entity.follow;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;

import java.time.LocalDateTime;

/**
 * FollowEntity 클래스
 * 이 클래스는 'follow' 테이블과 매핑되는 엔티티(Entity)로, 사용자가 다른 사용자를 팔로우하는 관계를 나타냅니다.
 * 이 엔티티는 팔로워(follower)와 팔로잉(following) 간의 팔로우 관계를 저장하고 관리합니다.
 */
@Data  // Lombok 어노테이션으로 getter, setter, toString, equals, hashCode 메서드를 자동으로 생성
@NoArgsConstructor  // 기본 생성자를 자동으로 생성하는 Lombok 어노테이션
@AllArgsConstructor  // 모든 필드를 포함한 생성자를 자동으로 생성하는 Lombok 어노테이션
@SuperBuilder  // 부모 클래스의 필드까지 빌더 패턴으로 생성 가능하도록 지원하는 Lombok 어노테이션
@Entity  // JPA에서 이 클래스가 데이터베이스 테이블과 매핑된다는 것을 나타냄
@Table(name = "follow")  // 이 클래스는 'follow' 테이블과 매핑됨을 나타냄
@IdClass(FollowId.class)  // 복합 키를 정의한 FollowId 클래스를 사용함
public class FollowEntity {


    @Id  // 이 필드가 테이블의 기본 키의 일부임을 나타냄
    @Column(name = "follower_id", nullable = false)  // 'follower_id' 컬럼과 매핑, null 값을 허용하지 않음
    private String followerId;  // 팔로우를 하는 사람의 ID

    @Id  // 이 필드도 기본 키의 일부임을 나타냄 (복합키)
    @Column(name = "following_id", nullable = false)  // 'following_id' 컬럼과 매핑, null 값을 허용하지 않음
    private String followingId;  // 팔로우 받는 사람의 ID

    @Column(name = "follow_date", nullable = false)  // 'follow_date' 컬럼과 매핑, null 값을 허용하지 않음
    private LocalDateTime followDate;  // 팔로우한 날짜와 시간

    // 팔로우 관계의 팔로워(follower)와 회원(MemberEntity)의 관계 설정 (다대일 관계)
    @ManyToOne
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)  // 해당 컬럼을 수정하거나 삽입할 수 없음
    private MemberEntity follower;  // 팔로우를 하는 회원 정보

    // 팔로우 관계의 팔로잉(following)과 회원(MemberEntity)의 관계 설정 (다대일 관계)
    @ManyToOne
    @JoinColumn(name = "following_id", insertable = false, updatable = false)  // 해당 컬럼을 수정하거나 삽입할 수 없음
    private MemberEntity following;  // 팔로우를 받는 회원 정보

    /**
     * 추가적인 생성자 (팔로우 ID와 팔로잉 ID를 받음)
     * @param followerId 팔로워의 ID
     * @param followingId 팔로잉의 ID
     * @param now 팔로우한 날짜 및 시간
     */
    public FollowEntity(String followerId, String followingId, LocalDateTime now) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.followDate = now;
    }
}
