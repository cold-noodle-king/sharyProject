package net.datasa.sharyproject.domain.entity.personal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "personal_like")
public class PersonalLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personal_like_num")
    private Integer personalLikeNum;  // 변경된 필드 이름

    @Column(name = "personal_note_num", nullable = false)
    private Integer personalNoteNum;

    @Column(name = "member_id", nullable = false, length = 50)
    private String memberId;

    @Column(name = "like_clicked", nullable = false)
    private boolean likeClicked;  // true면 좋아요가 눌린 상태
}