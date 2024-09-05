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
@Table(name = "share_play")
@EntityListeners(AuditingEntityListener.class)
public class SharePlay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_play_num")
    private Integer sharePlayNum;

    @Column(name = "music_num", nullable = false)
    private Integer musicNum;

    @Column(name = "share_note_num", nullable = false)
    private Integer shareNoteNum;

    @Column(name = "playing", nullable = false)
    private Boolean playing;

}
