package net.datasa.sharyproject.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hashtag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashtagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer hashtagNum; // 해시태그 번호 (Primary Key)

    @Column(length = 50)
    private String hashtagName; // 해시태그 이름

    // 외래키로 연결된 category 테이블 참조
    @ManyToOne
    @JoinColumn(name = "category_num", nullable = false)
    private CategoryEntity category; // 카테고리 정보 (외래키)
}
