package net.datasa.sharyproject.service;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.HashtagDTO;
import net.datasa.sharyproject.domain.entity.HashtagEntity;
import net.datasa.sharyproject.repository.HashtagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagService {

    // 해시태그 테이블에 접근하기 위한 Repository
    private final HashtagRepository hashtagRepository;

    /**
     * 특정 카테고리에 맞는 해시태그 목록을 가져오는 메서드
     *
     * @param categoryNum 카테고리 번호
     * @return HashtagDTO 객체의 리스트
     * 카테고리에 해당하는 해시태그 데이터를 HashtagDTO로 변환하여 반환합니다.
     */
    public List<HashtagDTO> getHashtagsByCategory(Integer categoryNum) {
        // 카테고리 번호에 해당하는 해시태그를 조회
        List<HashtagEntity> hashtags = hashtagRepository.findByCategory_CategoryNum(categoryNum);

        // HashtagEntity 리스트를 HashtagDTO 리스트로 변환하여 반환
        return hashtags.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * HashtagEntity를 HashtagDTO로 변환하는 메서드
     *
     * @param hashtagEntity 변환할 HashtagEntity 객체
     * @return HashtagDTO 객체
     */
    private HashtagDTO convertToDTO(HashtagEntity hashtagEntity) {
        // HashtagEntity에서 HashtagDTO로 변환
        return HashtagDTO.builder()
                .hashtagNum(hashtagEntity.getHashtagNum())  // 해시태그 ID
                .hashtagName(hashtagEntity.getHashtagName())  // 해시태그 이름
                .build();
    }
}