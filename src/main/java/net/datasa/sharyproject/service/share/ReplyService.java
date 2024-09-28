package net.datasa.sharyproject.service.share;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.share.ReplyDTO;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.share.ReplyEntity;
import net.datasa.sharyproject.domain.entity.share.ShareNoteEntity;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.share.ReplyRepository;
import net.datasa.sharyproject.repository.share.ShareNoteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
@Builder
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final ShareNoteRepository shareNoteRepository;

    public List<ReplyDTO> getList(Integer noteNum){

        ShareNoteEntity noteEntity = shareNoteRepository.findById(noteNum)
                .orElseThrow(() -> new EntityNotFoundException("노트를 찾을 수 없습니다."));

        List<ReplyDTO> replyList = new ArrayList<>();
        for (ReplyEntity replyEntity : noteEntity.getReplyList()) {
            ReplyDTO replyDTO = new ReplyDTO();
            replyDTO.setReplyNum(replyEntity.getReplyNum());
            replyDTO.setShareNoteNum(replyEntity.getShareNote().getShareNoteNum());
            replyDTO.setMemberId(replyEntity.getMember().getMemberId());
            replyDTO.setNickname(replyEntity.getMember().getNickname());
            replyDTO.setContents(replyEntity.getContents());
            replyDTO.setCreatedDate(replyEntity.getCreatedDate());
            replyList.add(replyDTO);
        }
        log.debug("리플 리스트:{}", replyList);
        return replyList;
    }

    public void save(ReplyDTO dto){

        log.debug("넘어왔나 보자:{}", dto);
        ShareNoteEntity shareNoteEntity = shareNoteRepository.findById(dto.getShareNoteNum())
                .orElseThrow(() -> new EntityNotFoundException("게시물이 존재하지 않습니다."));

        MemberEntity memberEntity = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("게시물이 존재하지 않습니다."));

        ReplyEntity entity = new ReplyEntity();

        entity.setMember(memberEntity);
        entity.setShareNote(shareNoteEntity);
        entity.setContents(dto.getContents());

        log.debug("저장되는 엔티티: {}", entity);

        replyRepository.save(entity);
    }

    public void delete(ReplyDTO dto) throws Exception{

        ReplyEntity entity = replyRepository.findById(dto.getReplyNum())
                .orElseThrow(() -> new RuntimeException("삭제할 수 없습니다."));

        if (entity.getMember().getMemberId().equals(dto.getMemberId())) {
            replyRepository.delete(entity);
        } else {
            throw new EntityNotFoundException("삭제할 수 없습니다.");
        }
    }


}
