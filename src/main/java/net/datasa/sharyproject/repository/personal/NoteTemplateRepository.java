package net.datasa.sharyproject.repository.personal;

import net.datasa.sharyproject.domain.entity.personal.NoteTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteTemplateRepository extends JpaRepository<NoteTemplateEntity, Integer> {
}
