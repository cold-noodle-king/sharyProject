package net.datasa.sharyproject.service.personal;

import net.datasa.sharyproject.domain.dto.personal.NoteTemplateDTO;
import net.datasa.sharyproject.domain.entity.personal.NoteTemplateEntity;
import net.datasa.sharyproject.repository.personal.NoteTemplateRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
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
                    // 이미지 경로를 웹 경로로 변환
                    String imagePath = convertToWebPath(entity.getNoteImage());

                    // 로그로 변환된 경로 확인
                    System.out.println("Converted image path: " + imagePath);

                    return NoteTemplateDTO.builder()
                            .noteNum(entity.getNoteNum())
                            .noteName(entity.getNoteName())
                            .noteImage(imagePath)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ID로 노트 템플릿을 가져옴
    public NoteTemplateDTO getNoteTemplateById(Integer noteNum) {
        Optional<NoteTemplateEntity> noteTemplate = noteTemplateRepository.findById(noteNum);
        if (noteTemplate.isPresent()) {
            NoteTemplateEntity entity = noteTemplate.get();
            return NoteTemplateDTO.builder()
                    .noteNum(entity.getNoteNum())
                    .noteName(entity.getNoteName())
                    .noteImage(convertToWebPath(entity.getNoteImage()))
                    .build();
        } else {
            throw new RuntimeException("노트 템플릿을 찾을 수 없습니다.");
        }
    }

    // 절대 경로를 웹 경로로 변환
    private String convertToWebPath(String filePath) {
        if (filePath.contains("src/main/resources/static/images/")) {
            return filePath.replace("C:/Java/workspace/sharyProject/src/main/resources/static/images/", "/images/");
        }
        return filePath;
    }
}