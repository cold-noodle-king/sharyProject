package net.datasa.sharyproject.repository;

import net.datasa.sharyproject.domain.entity.HashtagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagRepository extends JpaRepository<HashtagEntity, Integer> {
}
