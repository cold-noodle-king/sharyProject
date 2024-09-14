package net.datasa.sharyproject.domain.entity.follow;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "follow")
@IdClass(FollowId.class)    // 복합키 정의 클래스
public class FollowEntity {

    @Id
    @Column(name = "follower_id", nullable = false)
    private String followerId;

    @Id
    @Column(name = "following_id", nullable = false)
    private String followingId;

    @Column(name = "follow_date", nullable = false)
    private LocalDateTime followDate;

    // Member와의 관계 설정
    @ManyToOne
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)
    private MemberEntity follower;

    @ManyToOne
    @JoinColumn(name = "following_id", insertable = false, updatable = false)
    private MemberEntity following;

    public FollowEntity(String followerId, String followingId, LocalDateTime now) {
    }
}
