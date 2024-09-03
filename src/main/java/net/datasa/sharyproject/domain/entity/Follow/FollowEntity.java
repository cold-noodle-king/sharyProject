package net.datasa.sharyproject.domain.entity.Follow;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "follow")
@IdClass(FollowId.class)    //복합키 정의 클래스
public class FollowEntity {


    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "follower_id", referencedColumnName = "follower_id", insertable = false, updatable = false),
            @JoinColumn(name = "following_id", referencedColumnName = "following_id", insertable = false, updatable = false)
    })
    private FollowEntity follow;

    @Id
    @Column(name = "follower_id", nullable = false)
    private String followerId;


    @Id
    @Column(name = "following_id", nullable = false)
    private String followingId;

    @Column(name = "follow_date", nullable = false)
    private LocalDateTime followDate;

}
