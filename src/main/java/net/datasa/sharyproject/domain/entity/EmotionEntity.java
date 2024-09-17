package net.datasa.sharyproject.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;

import java.lang.reflect.Member;

@Entity
@Table(name = "emotion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int emotionNum; // 감정 번호 (Primary Key)

    @Column(nullable = false)
    private String emotionName; // 감정 이름 (예: 기쁨, 슬픔 등)

    // 외래키로 연결된 member 테이블 참조
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member; // 사용자 정보 (외래키)
}
