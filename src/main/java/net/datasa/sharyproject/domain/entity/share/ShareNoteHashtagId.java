package net.datasa.sharyproject.domain.entity.share;

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
public class ShareNoteHashtagId implements Serializable {

    private int shareNoteNum;  // 노트 번호 (FK)
    private int hashtagNum;       // 해시태그 번호 (FK)

    // equals와 hashCode는 반드시 구현해야 합니다.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShareNoteHashtagId that = (ShareNoteHashtagId) o;
        return shareNoteNum == that.shareNoteNum && hashtagNum == that.hashtagNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shareNoteNum, hashtagNum);
    }
}
