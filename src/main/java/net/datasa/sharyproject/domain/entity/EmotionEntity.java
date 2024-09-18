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
    private Integer emotionNum; // 감정 번호 (Primary Key)

    @Column(nullable = false)
    private String emotionName; // 감정 이름 (예: 기쁨, 슬픔 등)

    // 파라미터를 받는 생성자 추가
    public EmotionEntity(Integer emotionNum) {
        this.emotionNum = emotionNum;
    }

}
