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

    /**
     * 공유 다이어리 노트에 달린 댓글을 출력하는 메서드
     * @param noteNum
     * @return
     */
    @ResponseBody
    @PostMapping("commentList")
    public List<ReplyDTO> getList(@RequestParam("noteNum") Integer noteNum) {

        List<ReplyDTO> list = replyService.getList(noteNum);
        log.debug("댓글 리스트 확인:{}", list);
        return list;
    }

    /**
     * 사용자가 작성한 댓글을 저장 및 출력하는 메서드
     * @param user
     * @param dto
     */
    @ResponseBody
    @PostMapping("write")
    public void write(@AuthenticationPrincipal AuthenticatedUser user,
                      @ModelAttribute ReplyDTO dto) {


        dto.setMemberId(user.getUsername());
        log.debug("정보를보여줘:{}", dto);

        replyService.save(dto);
    }

    /**
     * 댓글을 삭제하는 메서드
     * @param user
     * @param noteNum
     * @param replyNum
     */
    @ResponseBody
    @PostMapping("delete")
    public void delete(@AuthenticationPrincipal AuthenticatedUser user,
                       @RequestParam("noteNum") Integer noteNum,
                       @RequestParam("replyNum") Integer replyNum){

        log.debug("노트번호:{}, 댓글번호:{}", noteNum, replyNum);

        ReplyDTO dto = new ReplyDTO();

        dto.setShareNoteNum(noteNum);
        dto.setReplyNum(replyNum);
        dto.setMemberId(user.getUsername());
        log.debug("dto가 뭐니:{}", dto);


        try {
            replyService.delete(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}