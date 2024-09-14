package net.datasa.sharyproject.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private int messageId;
    private int chatId;
    private String messageContent;
    private LocalDateTime createdDate;
    private String senderId;
}
