package net.datasa.sharyproject.domain.dto.member;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.sharyproject.domain.entity.CategoryEntity;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCategoryDTO {

    private Integer userCategoryNum;

    private MemberEntity member;

    private CategoryEntity category;
}
