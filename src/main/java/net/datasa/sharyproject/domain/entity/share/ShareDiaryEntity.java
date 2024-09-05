package net.datasa.sharyproject.domain.entity.share;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "share_diary")
@EntityListeners(AuditingEntityListener.class)
public class ShareDiaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_diary_num")
    private Integer shareDiaryNum;

    @Column(name = "share_diary_name", nullable = false, length = 100)
    private String shareDiaryName;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @Column(name = "category_num", nullable = false)
    private Integer categoryNum;

    @Column(name = "cover_num", nullable = false)
    private Integer coverNum;

    @Column(name = "member_id", nullable = false, length = 50)
    private String memberId;
}
