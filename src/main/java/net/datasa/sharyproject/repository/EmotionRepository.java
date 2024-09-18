package net.datasa.sharyproject.repository;

import net.datasa.sharyproject.domain.entity.EmotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionRepository extends JpaRepository<EmotionEntity, Integer> {
}
