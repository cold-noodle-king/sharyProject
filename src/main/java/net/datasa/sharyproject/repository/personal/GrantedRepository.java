package net.datasa.sharyproject.repository.personal;

import net.datasa.sharyproject.domain.entity.personal.GrantedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrantedRepository extends JpaRepository<GrantedEntity, Integer> {
}
