package net.datasa.sharyproject.domain.entity.share;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "share_like")
@IdClass(ShareLikes.class)    //복합키 정의 클래스
public class ShareLikeEntity {

    @Id
    @Column(name = "share_note_num")
    private Integer shareNoteNum;

    @Id
    @Column(name = "liked_num")
    private Integer likedNum;
}
