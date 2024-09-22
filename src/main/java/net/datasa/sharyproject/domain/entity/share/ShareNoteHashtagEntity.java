package net.datasa.sharyproject.domain.entity.share;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.sharyproject.domain.entity.HashtagEntity;

@Entity
@Table(name = "share_note_hashtag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareNoteHashtagEntity {

    @EmbeddedId  // 복합 키를 사용
    private ShareNoteHashtagId id;

    @ManyToOne
    @MapsId("shareNoteNum")  // 복합 키의 shareNoteNum을 매핑
    @JoinColumn(name = "share_note_num")
    private ShareNoteEntity shareNote;

    @ManyToOne
    @MapsId("hashtagNum")  // 복합 키의 hashtagNum을 매핑
    @JoinColumn(name = "hashtag_num")
    private HashtagEntity hashtag;

}