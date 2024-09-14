package net.datasa.sharyproject.domain.entity.chat;

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
    private int messageId;

    @Column(name = "chat_id", nullable = false)
    private int chatId;

    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "sender_id", nullable = false)
    private String senderId;
}