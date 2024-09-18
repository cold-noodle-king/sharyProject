package net.datasa.sharyproject.domain.entity.personal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.sharyproject.domain.entity.HashtagEntity;

@Entity
@Table(name = "personal_note_hashtag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalNoteHashtagEntity {

    @EmbeddedId  // 복합 키를 사용
    private PersonalNoteHashtagId id;

    @ManyToOne
    @MapsId("personalNoteNum")  // 복합 키의 personalNoteNum을 매핑
    @JoinColumn(name = "personal_note_num")
    private PersonalNoteEntity personalNote;

    @ManyToOne
    @MapsId("hashtagNum")  // 복합 키의 hashtagNum을 매핑
    @JoinColumn(name = "hashtag_num")
    private HashtagEntity hashtag;
}