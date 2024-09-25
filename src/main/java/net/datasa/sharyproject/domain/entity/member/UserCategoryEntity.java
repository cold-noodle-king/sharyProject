package net.datasa.sharyproject.domain.entity.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.datasa.sharyproject.domain.entity.CategoryEntity;

import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "user_category")
public class UserCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_category_num", nullable = false)
    private Integer userCategoryNum;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "category_num", referencedColumnName = "category_num", nullable = false)
    private CategoryEntity category;


}
