package net.datasa.sharyproject.controller.personal;

import net.datasa.sharyproject.domain.dto.cover.CoverTemplateDTO;
import net.datasa.sharyproject.service.cover.CoverTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("personal")
@Controller
public class MyDiaryController {

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
        return "personal/CoverSelect";  // "personal/CoverSelect"가 실제 커버 선택 페이지의 뷰 이름이어야 합니다.
    }

}