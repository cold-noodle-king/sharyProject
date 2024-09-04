package net.datasa.sharyproject.service.cover;

import net.datasa.sharyproject.domain.dto.personal.CoverTemplateDTO;
import net.datasa.sharyproject.repository.personal.CoverTemplateRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoverTemplateService {

    private final CoverTemplateRepository coverTemplateRepository;

    // 커버 템플릿 리스트를 가져오는 메서드
    public List<CoverTemplateDTO> getCoverTemplates() {
        // DB에서 커버 템플릿 데이터를 가져와서 DTO로 변환
        return coverTemplateRepository.findAll().stream()
                .map(entity -> CoverTemplateDTO.builder()
                        .coverNum(entity.getCoverNum())
                        .coverName(entity.getCoverName())
                        // DB의 파일 시스템 경로를 웹 경로로 변환
                        .coverImage(entity.getCoverImage().replace("C:/Java/workspace/sharyProject/src/main/resources/static/images/", "/images/"))  // 수정된 부분
                        .build())
                .collect(Collectors.toList());
    }
}