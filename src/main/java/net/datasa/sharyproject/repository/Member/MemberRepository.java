package net.datasa.sharyproject.repository.Member;

import net.datasa.sharyproject.domain.entity.Member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String> {

}
