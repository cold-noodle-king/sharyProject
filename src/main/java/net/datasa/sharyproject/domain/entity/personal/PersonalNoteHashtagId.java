package net.datasa.sharyproject.domain.entity.personal;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalNoteHashtagId implements Serializable {

    private int personalNoteNum;  // 노트 번호 (FK)
    private int hashtagNum;       // 해시태그 번호 (FK)

    // equals와 hashCode는 반드시 구현해야 합니다.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalNoteHashtagId that = (PersonalNoteHashtagId) o;
        return personalNoteNum == that.personalNoteNum && hashtagNum == that.hashtagNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(personalNoteNum, hashtagNum);
    }
}
