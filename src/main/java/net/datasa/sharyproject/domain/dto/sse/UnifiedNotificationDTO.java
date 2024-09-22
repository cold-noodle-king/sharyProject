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
public class UnifiedNotificationDTO {
    private String type; // "message" 또는 "notification"
    private String sender; // 메시지의 경우 발신자
    private String content;
    private LocalDateTime createdAt;
}