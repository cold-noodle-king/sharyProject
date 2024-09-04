package net.datasa.sharyproject.controller.personal;

import net.datasa.sharyproject.domain.dto.personal.CoverTemplateDTO;
import net.datasa.sharyproject.service.cover.CoverTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("personal")
@Controller
public class MyDiaryController {

    private final CoverTemplateService coverTemplateService;

    @GetMapping("MyDiary")
    public String MyDiary() {
        return "personal/MyDiary";
    }

    @GetMapping("categorySelect")
    public String categorySelect() {
        return "personal/PersonalCategorySelect";
    }

    // 커버 선택 페이지로 이동하는 메서드 추가
    @GetMapping("cover")
    public String cover() {
        return "personal/CoverSelect";
    }

    // 커버 템플릿 데이터를 제공하는 API
    @GetMapping("getCoverTemplates")
    @ResponseBody
    public List<CoverTemplateDTO> getCoverTemplates() {
        return coverTemplateService.getCoverTemplates(); // 커버 템플릿 리스트 반환
    }

}