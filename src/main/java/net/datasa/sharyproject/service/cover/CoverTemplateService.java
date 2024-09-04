package net.datasa.sharyproject.service.cover;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.cover.CoverTemplateDTO;
import net.datasa.sharyproject.repository.cover.CoverTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class CoverTemplateService {

    // CoverTemplateRepository를 주입받는 final 필드
    private final CoverTemplateRepository coverTemplateRepository;


}