package net.datasa.sharyproject.service.personal;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.personal.GrantedDTO;
import net.datasa.sharyproject.domain.entity.personal.GrantedEntity;
import net.datasa.sharyproject.repository.personal.GrantedRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrantedService {

    // 공개 권한 테이블에 접근하기 위한 Repository
    private final GrantedRepository grantedRepository;

    /**
     * 모든 공개 권한 목록을 가져오는 메서드
     *
     * @return GrantedDTO 객체의 리스트
     * 모든 공개 권한 데이터를 GrantedDTO로 변환하여 반환합니다.
     * 이를 통해 사용자에게 공개 권한 선택 옵션을 제공합니다.
     */
    public List<GrantedDTO> getAllPermissions() {
        // 공개 권한 엔티티를 데이터베이스에서 조회
        List<GrantedEntity> permissions = grantedRepository.findAll();

        // GrantedEntity 리스트를 GrantedDTO 리스트로 변환하여 반환
        return permissions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * GrantedEntity를 GrantedDTO로 변환하는 메서드
     *
     * @param grantedEntity 변환할 GrantedEntity 객체
     * @return GrantedDTO 객체
     */
    private GrantedDTO convertToDTO(GrantedEntity grantedEntity) {
        // GrantedEntity에서 GrantedDTO로 변환
        return GrantedDTO.builder()
                .grantedNum(grantedEntity.getGrantedNum())  // 공개 권한 ID
                .grantedName(grantedEntity.getGrantedName())  // 공개 권한 이름
                .build();
    }
}
