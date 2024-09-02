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
@Table(name = "chat_message")
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Integer messageId;

    @Column(name = "message_content", nullable = false)
    private String messageContent;

    @Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;
}
