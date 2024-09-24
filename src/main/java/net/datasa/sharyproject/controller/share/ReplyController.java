package net.datasa.sharyproject.controller.share;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.share.ReplyDTO;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.share.ReplyService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("reply")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReplyController {

    private final ReplyService replyService;

    @ResponseBody
    @PostMapping("commentList")
    public List<ReplyDTO> getList(@RequestParam("noteNum") Integer noteNum) {

        return replyService.getList(noteNum);
    }

    @ResponseBody
    @PostMapping("write")
    public void write(@AuthenticationPrincipal AuthenticatedUser user,
                      @ModelAttribute ReplyDTO dto) {


        dto.setMemberId(user.getUsername());
        log.debug("정보를보여줘:{}", dto);

        replyService.save(dto);
    }

    /*@ResponseBody
    @PostMapping("delete")
    public void delete(@AuthenticationPrincipal AuthenticatedUser user,
                       @RequestParam("bnum") Integer bnum,
                       @RequestParam("rnum") Integer rnum){

        ReplyDTO dto = new ReplyDTO();

        dto.setBoardNum(bnum);
        dto.setReplyNum(rnum);
        dto.setMemberId(user.getUsername());
        log.debug("dto가 뭐니:{}", dto);


        try {
            replyService.delete(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @ResponseBody
    @PostMapping("update")
    public void udpate(@ModelAttribute ReplyDTO dto
            ,@AuthenticationPrincipal AuthenticatedUser user){

        dto.setMemberId(user.getUsername());
        log.debug("수정할 댓글: {}", dto);

        replyService.update(dto);

    }*/
}