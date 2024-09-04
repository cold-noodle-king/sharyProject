package net.datasa.sharyproject.domain.entity.personal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "cover_template")
public class CoverTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cover_num")
    private Integer coverNum;

    @Column(name = "cover_name")
    private String coverName;

    @Column(name = "cover_image")
    private String coverImage;
}
