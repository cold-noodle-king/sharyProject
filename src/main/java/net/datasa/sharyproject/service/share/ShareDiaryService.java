package net.datasa.sharyproject.service.share;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.repository.share.ShareDiaryRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ShareDiaryService {

    private final ShareDiaryRepository shareDiaryRepository;



}
