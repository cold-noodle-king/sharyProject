package net.datasa.sharyproject.domain.dto.sse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;
    private String receiverId;
    private String content;
    private LocalDateTime createdAt;
    private boolean isRead;
}
