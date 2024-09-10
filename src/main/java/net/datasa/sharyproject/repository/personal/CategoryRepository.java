package net.datasa.sharyproject.repository.personal;

import net.datasa.sharyproject.domain.entity.personal.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
}
