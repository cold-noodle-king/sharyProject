package net.datasa.sharyproject.domain.entity.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // Lombok으로 게터와 세터 자동 생성
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat")
public class ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id", nullable = false)
    private int chatId;

    @Column(name = "participant1_id", nullable = false)
    private String participant1Id;

    @Column(name = "participant2_id", nullable = false)
    private String participant2Id;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
}
