package net.datasa.sharyproject.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// ChatDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    private int chatId;
    private String participant1Id;
    private String participant2Id;
    private LocalDateTime createdDate;
}