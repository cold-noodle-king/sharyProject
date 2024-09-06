package net.datasa.sharyproject.domain.entity.share;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "share_member")
@EntityListeners(AuditingEntityListener.class)
public class ShareMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_member_num")
    private Integer shareMemberNum;

    @Column(name = "member_id", nullable = false, length = 50)
    private String memberId;

    @Column(name = "share_diary_num", nullable = false)
    private Integer shareDiaryNum;

    @Column(name = "manager_id", nullable = false, length = 50)
    private String managerId;

}
