package net.datasa.sharyproject.service;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.EmotionDTO;
import net.datasa.sharyproject.domain.entity.EmotionEntity;
import net.datasa.sharyproject.repository.EmotionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmotionService {

    // 감정 테이블에 접근하기 위한 Repository
    private final EmotionRepository emotionRepository;

    /**
     * 모든 감정 목록을 가져오는 메서드
     *
     * @return EmotionDTO 객체의 리스트
     * 모든 감정 데이터를 EmotionDTO로 변환하여 반환합니다.
     * 이를 통해 사용자에게 감정 선택 옵션을 제공합니다.
     */
    public List<EmotionDTO> getAllEmotions() {
        // 감정 엔티티를 데이터베이스에서 조회
        List<EmotionEntity> emotions = emotionRepository.findAll();

        // EmotionEntity 리스트를 EmotionDTO 리스트로 변환하여 반환
        return emotions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * EmotionEntity를 EmotionDTO로 변환하는 메서드
     *
     * @param emotionEntity 변환할 EmotionEntity 객체
     * @return EmotionDTO 객체
     */
    private EmotionDTO convertToDTO(EmotionEntity emotionEntity) {
        // EmotionEntity에서 EmotionDTO로 변환
        return EmotionDTO.builder()
                .emotionNum(emotionEntity.getEmotionNum())  // 감정 ID
                .emotionName(emotionEntity.getEmotionName())  // 감정 이름
                .build();
    }
}
