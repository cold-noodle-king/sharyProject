package net.datasa.sharyproject.domain.entity.personal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "granted")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrantedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer grantedNum; // 권한 번호 (Primary Key)

    @Column(nullable = false, length = 30)
    private String grantedName; // 권한 이름 (예: 읽기, 쓰기 등)

    // 파라미터를 받는 생성자 추가
    public GrantedEntity(Integer grantedNum) {
        this.grantedNum = grantedNum;
    }
}
