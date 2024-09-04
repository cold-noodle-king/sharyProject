package net.datasa.sharyproject.service.personal;

import net.datasa.sharyproject.domain.dto.personal.NoteTemplateDTO;
import net.datasa.sharyproject.repository.personal.NoteTemplateRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteTemplateService {

    private final NoteTemplateRepository noteTemplateRepository;

    // 노트 템플릿 리스트를 가져오는 메서드
    public List<NoteTemplateDTO> getNoteTemplates() {
        // DB에서 노트 템플릿 데이터를 가져와서 DTO로 변환
        return noteTemplateRepository.findAll().stream()
                .map(entity -> {
                    // 이미지 경로를 변환하고, 로그를 통해 확인
                    String imagePath = entity.getNoteImage().replace("C:/Java/workspace/sharyProject/src/main/resources/static/images/", "/images/");
                    // 경로 변환 로그 출력
                    System.out.println("Converted image path: " + imagePath);

                    return NoteTemplateDTO.builder()
                            .noteNum(entity.getNoteNum())
                            .noteName(entity.getNoteName())
                            .noteImage(imagePath)
                            .build();
                })
                .collect(Collectors.toList());
    }
}