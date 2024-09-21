package net.datasa.sharyproject.service.sse;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.entity.sse.SseMessageEntity;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.sse.SseMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SseMessageService {

    private final MemberRepository memberRepository;
    private final SseMessageRepository sseMessageRepository;

    // 메시지를 DB에 저장 (기존)
    @Transactional
    public void saveMessage(String fromId, String toId, String content) {
        SseMessageEntity messageEntity = SseMessageEntity.builder()
                .fromMember(memberRepository.findById(fromId).orElseThrow())
                .toMember(memberRepository.findById(toId).orElseThrow())
                .content(content)
                .createDate(LocalDateTime.now())
                .build();

        sseMessageRepository.save(messageEntity);
    }


    // 로그인한 사용자가 받은 메시지를 DB에서 조회하는 로직
    @Transactional(readOnly = true)
    public List<String> getMessages(String memberId) {
        // 수신자가 로그인한 사용자인 메시지만 조회
        List<SseMessageEntity> messages = sseMessageRepository.findByToMember_MemberId(memberId);

        // 메시지를 JSON 형식으로 반환
        return messages.stream()
                .map(message -> String.format("{\"sender\":\"%s\", \"message\":\"%s\"}",
                        message.getFromMember().getMemberId(),
                        message.getContent()))
                .collect(Collectors.toList());
    }
}
