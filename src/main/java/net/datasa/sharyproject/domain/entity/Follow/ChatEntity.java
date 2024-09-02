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
@Table(name = "chat")
@IdClass(ChatId.class)
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id", nullable = false)
    private Integer chatId;

    @Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @Column(name = "updated_date", columnDefinition = "TIMESTAMP NULL")
    private LocalDateTime updatedDate;

    @Id
    @Column(name = "follower_id", nullable = false)
    private String followerId;

    @Id
    @Column(name = "following_id", nullable = false)
    private String followingId;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "follower_id", referencedColumnName = "follower_id", insertable = false, updatable = false),
            @JoinColumn(name = "following_id", referencedColumnName = "following_id", insertable = false, updatable = false)
    })
    private FollowEntity follow;
}
