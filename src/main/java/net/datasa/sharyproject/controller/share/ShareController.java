package net.datasa.sharyproject.controller.share;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.EmotionDTO;
import net.datasa.sharyproject.domain.dto.HashtagDTO;
import net.datasa.sharyproject.domain.dto.mypage.ProfileDTO;
import net.datasa.sharyproject.domain.dto.personal.*;
import net.datasa.sharyproject.domain.dto.share.*;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.domain.entity.personal.NoteTemplateEntity;
import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.mypage.ProfileRepository;
import net.datasa.sharyproject.repository.personal.NoteTemplateRepository;
import net.datasa.sharyproject.repository.share.ShareDiaryRepository;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.EmotionService;
import net.datasa.sharyproject.service.HashtagService;
import net.datasa.sharyproject.service.mypage.ProfileService;
import net.datasa.sharyproject.service.personal.CoverTemplateService;
import net.datasa.sharyproject.service.personal.NoteTemplateService;
import net.datasa.sharyproject.service.share.ReplyService;
import net.datasa.sharyproject.service.share.ShareDiaryService;
import net.datasa.sharyproject.service.share.ShareNoteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("share")
@RequiredArgsConstructor
public class ShareController {

    private final CoverTemplateService coverTemplateService;
    private final NoteTemplateService noteTemplateService;
    private final ShareDiaryService shareDiaryService;
    private final ShareDiaryRepository shareDiaryRepository;
    private final HashtagService hashtagService;
    private final EmotionService emotionService;
    private final ShareNoteService shareNoteService;
    private final ProfileService profileService;
    private final NoteTemplateRepository noteTemplateRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final ReplyService replyService;

    // 파일 업로드 디렉토리 설정 (application.properties에서 주입)
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 공유 다이어리 메인 페이지로 이동
     *
     * @param user
     * @param model
     * @return
     */
    @GetMapping("main")
    public String viewMain(@AuthenticationPrincipal AuthenticatedUser user, Model model) {

        //내가 생성한 다이어리 리스트를 불러오는 메서드
        List<ShareDiaryDTO> createdDiaries = shareDiaryService.getCreatedList(user.getUsername());

        log.debug("불러온 다이어리들 정보:{}", createdDiaries);
        model.addAttribute("diaryList", createdDiaries);
        model.addAttribute("activePage", "created");


        return "share/Main";
    }

    /**
     * 내가 생성한 공유 다이어리로 이동
     *
     * @param diaryNum
     * @param user
     * @param model
     * @return
     */
    @GetMapping("createdDiary")
    public String viewCreatedDiary(@RequestParam("diaryNum") Integer diaryNum
                                 , @AuthenticationPrincipal AuthenticatedUser user
                                 , Model model) {

        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum);
        log.debug("가져온 다이어리 정보:{}", dto);
        List<ShareNoteDTO> dtoList = shareNoteService.getNotesByDiaryNum(diaryNum);
        log.debug("리스트를 제대로 갖고왔니?{}", dtoList);
        // 멤버 수 가져오기
        int memberCount = shareDiaryService.getMemberCount(diaryNum);


        model.addAttribute("diary", dto);
        model.addAttribute("noteList", dtoList);
        model.addAttribute("user", user);
        model.addAttribute("memberCount", memberCount);


        return "share/createdDiary";
    }

    /**
     * 내가 가입한 공유 다이어리 리스트 페이지 출력
     *
     * @param user
     * @param model
     * @return
     */
    @GetMapping("joinedList")
    public String joined(@AuthenticationPrincipal AuthenticatedUser user, Model model) {

        List<ShareDiaryDTO> joinedDiaries = shareDiaryService.getJoinedList(user.getUsername());
        log.debug("가입한 다이어리 리스트:{}", joinedDiaries);
        model.addAttribute("diaryList", joinedDiaries);
        model.addAttribute("activePage", "joined");

        return "share/JoinedList";
    }

    /**
     * 내가 가입한 공유 다이어리로 이동
     *
     * @param diaryNum
     * @param user
     * @param model
     * @return
     */
    @GetMapping("joinedDiary")
    public String joinedDiary(@RequestParam("diaryNum") Integer diaryNum
            , @AuthenticationPrincipal AuthenticatedUser user
            , Model model) {

        ShareDiaryDTO dto = shareDiaryService.getJoinedDiary(diaryNum, user.getUsername());
        List<ShareNoteDTO> dtoList = shareNoteService.getNotesByDiaryNum(diaryNum);
        log.debug("가져온 다이어리 정보:{}", dto);
        // 멤버 수 가져오기
        int memberCount = shareDiaryService.getMemberCount(diaryNum);

        model.addAttribute("diary", dto);
        model.addAttribute("noteList", dtoList);
        model.addAttribute("memberCount", memberCount);


        return "share/JoinedDiary";
    }


    /**
     * 다이어리 카테고리 선택 페이지로 이동
     *
     * @return
     */
    @GetMapping("categorySelect")
    public String categorySelect() {
        return "share/CategorySelect";
    }

    //리스트 형태로 받아온 카테고리를 문자열로 변환하는 메서드
    public class CategoryUtil {

        // 리스트를 쉼표로 구분된 문자열로 변환
        public static String listToString(List<String> categories) {
            return String.join(", ", categories);  // 쉼표와 공백으로 구분하여 문자열 생성
        }
    }

    /**
     * 다이어리 커버를 선택하는 메서드
     *
     * @param categories
     * @param model
     * @return
     */
    @PostMapping("coverSelect")
    public String coverSelect(@RequestParam("categories") List<String> categories, Model model) {
        log.debug("지정한 카테고리: {}", categories);

        String categoryName = CategoryUtil.listToString(categories);
        log.debug("문자열로 변환한 카테고리 이름:{}", categoryName);

        model.addAttribute("categoryName", categoryName);

        return "share/CoverSelect";
    }

    /**
     * 다이어리를 DB에 저장하는 메서드
     *
     * @param shareDiaryDTO
     * @param user
     * @param redirectAttributes
     * @return
     */
    @PostMapping("saveDiary")
    public String saveDiary(@ModelAttribute ShareDiaryDTO shareDiaryDTO
            , @AuthenticationPrincipal AuthenticatedUser user
            , RedirectAttributes redirectAttributes) {

        log.debug("컨틀롤러로 갔는지 확인:{}", shareDiaryDTO);
        ShareDiaryEntity entity = shareDiaryService.saveDiary(shareDiaryDTO, user);
        log.debug("엔티티로 잘 넘어왔니?:{}", entity);

        redirectAttributes.addFlashAttribute("msg", "다이어리가 저장되었습니다. 이어서 노트를 작성하시겠습니까?");
        redirectAttributes.addFlashAttribute("diaryNum", entity.getShareDiaryNum());

        return "redirect:/share/main";
    }

    /**
     * 다이어리 관리 페이지로 이동
     *
     * @param diaryNum
     * @param user
     * @param model
     * @return
     */
    @GetMapping("manageDiary")
    public String manageDiary(@RequestParam("diaryNum") Integer diaryNum
            , @AuthenticationPrincipal AuthenticatedUser user
            , Model model) {

        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum);
        model.addAttribute("diary", dto);

        return "share/manageDiary";
    }

    /**
     * 공유 다이어리 관리자가 전체 게시물을 확인할 수 있는 메서드
     * @param diaryNum
     * @return
     */
    @GetMapping("viewBoard")
    public String viewBoard(@RequestParam("diaryNum") Integer diaryNum, Model model){

        List<ShareNoteDTO> dtoList = shareNoteService.getNotesByDiaryNum(diaryNum);
        model.addAttribute("noteList", dtoList);

        return "share/viewBoard";
    }

    /**
     * 공유 다이어리 관리자가 전체 댓글을 확인할 수 있는 메서드
     * @param diaryNum
     * @param model
     * @return
     */
    @GetMapping("viewReply")
    public String viewReply(@RequestParam("diaryNum") Integer diaryNum, Model model){

        List<ShareNoteDTO> dtoList = shareNoteService.getNotesByDiaryNum(diaryNum);
        model.addAttribute("noteList", dtoList);

        List<ReplyDTO> replyList = replyService.getReply(diaryNum);
        model.addAttribute("replyList", replyList);
        log.debug("댓글 리스트 정보:{}", replyList);

        return "share/viewReply";
    }

    /**
     * 내가 생성한 공유다이어리를 삭제하는 메서드
     * @return
     */
    @GetMapping("deleteDiary")
    public String deleteDiary(@RequestParam("diaryNum") Integer diaryNum) {

        shareDiaryService.deleteDiary(diaryNum);

        return "share/main";
    }


    /**
     * 공유다이어리 정보 수정 페이지 출력
     *
     * @param diaryNum
     * @param user
     * @param model
     * @return
     */
    @GetMapping("infoUpdate")
    public String infoUpdate(@RequestParam("diaryNum") Integer diaryNum
            , @AuthenticationPrincipal AuthenticatedUser user
            , Model model) {

        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum);
        model.addAttribute("diary", dto);

        return "share/infoUpdate";
    }

    /**
     * 공유 다이어리 소개 멘트를 수정하는 메서드
     * @param diaryNum
     * @param diaryBio
     * @param user
     * @param model
     * @return
     */
    @PostMapping("bio")
    public String bio(@RequestParam("diaryNum") Integer diaryNum
            , @RequestParam("diaryBio") String diaryBio
            , @AuthenticationPrincipal AuthenticatedUser user
            , Model model) {

        shareDiaryService.updateBio(diaryNum, diaryBio, user.getUsername());

        return "redirect:/share/manageDiary?diaryNum=" + diaryNum;
    }

    /**
     * 다이어리 커버를 가져오는 메서드
     * @return
     */
    @GetMapping("getCoverTemplates")
    @ResponseBody
    public List<CoverTemplateDTO> getCoverTemplates() {
        return coverTemplateService.getCoverTemplates(); // 커버 템플릿 리스트 반환
    }

    /**
     * 노트 선택 페이지로 이동하는 메서드
     *
     * @return 노트 선택 페이지
     */
    @GetMapping("note")
    public String note(@RequestParam(value = "diaryNum", required = false) Integer diaryNum, Model model) {
        log.debug("다이어리 넘버 확인:{}", diaryNum);
        if (diaryNum == null) {
            throw new RuntimeException("Diary number is missing");
        }
        model.addAttribute("diaryNum", diaryNum);
        return "share/NoteSelect";  // 노트 템플릿 선택 페이지로 이동
    }

    /**
     * 노트 템플릿 데이터를 제공하는 API
     * @return
     */
    @GetMapping("getNoteTemplates")
    @ResponseBody
    public List<NoteTemplateDTO> getNoteTemplates() {
        return noteTemplateService.getNoteTemplates();  // 노트 템플릿 리스트 반환
    }

    /**
     * 노트 템플릿을 기반으로 다이어리 작성 페이지로 이동
     *
     * @param noteNum   노트 번호
     * @param diaryNum  다이어리 번호
     * @param noteName  노트 이름
     * @param model     모델 객체
     * @param principal 현재 로그인한 사용자 정보
     * @return 다이어리 작성 페이지 뷰
     */
    @GetMapping("noteForm")
    public String createDiary(@RequestParam("noteNum") Integer noteNum,
                              @RequestParam("diaryNum") Integer diaryNum,
                              @RequestParam("noteName") String noteName,
                              Model model, Principal principal) {

        // 필요한 파라미터가 제대로 전달되었는지 로그로 확인
        log.info("Received noteNum: {}, diaryNum: {}, noteName: {}", noteNum, diaryNum, noteName);


        // 노트 템플릿 정보 가져오기
        NoteTemplateDTO noteTemplate = noteTemplateService.getNoteTemplateById(noteNum);
        if (noteTemplate == null || noteTemplate.getNoteImage() == null) {
            throw new RuntimeException("노트 템플릿 또는 이미지 경로가 존재하지 않습니다.");
        }

        // 다이어리 정보 가져오기
        ShareDiaryDTO diary = shareDiaryService.getDiary(diaryNum);

        // 감정 목록 가져오기
        List<EmotionDTO> emotions = emotionService.getAllEmotions();

        // 다이어리 카테고리에 맞는 해시태그 목록 가져오기
        Integer categoryNum = diary.getCategoryNum();
        List<HashtagDTO> hashtags = hashtagService.getHashtagsByCategory(categoryNum);

        // 현재 로그인한 사용자의 프로필 정보 가져오기
        String memberId = principal.getName();
        ProfileDTO profile = profileService.getProfileByMemberId(memberId); // 프로필 정보 가져오기

        // 모델에 데이터 추가
        model.addAttribute("noteTemplate", noteTemplate);
        model.addAttribute("diaryNum", diaryNum);
        model.addAttribute("noteName", noteName);
        model.addAttribute("emotions", emotions);
        model.addAttribute("hashtags", hashtags);
        model.addAttribute("profile", profile); // 프로필 정보 추가

        return "share/NoteForm";
    }

    /**
     * 노트 정보를 가져오는 메서드 (Ajax 호출)
     *
     * @param noteNum 노트 번호
     * @return ShareNoteDTO
     */
    @GetMapping("/viewNote/{noteNum}")
    @ResponseBody
    public ResponseEntity<ShareNoteLikeResponseDTO> viewNote(@PathVariable("noteNum") Integer noteNum,
                                                             @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            // 노트 정보 가져오기
            ShareNoteDTO note = shareNoteService.getNoteByNum(noteNum);

            // 좋아요 정보 가져오기 (현재 사용자가 해당 노트를 좋아요 눌렀는지 여부 포함)
            LikeResponseDTO likeResponse = shareNoteService.getLikeInfo(noteNum, user.getUsername());

            // 해시태그 정보 추가
            List<String> hashtags = shareNoteService.getHashtagsByNoteNum(noteNum);
            note.setHashtags(hashtags);

            // 응답 DTO 생성
            ShareNoteLikeResponseDTO response = new ShareNoteLikeResponseDTO();
            response.setShareNote(note);
            response.setLikeResponse(likeResponse);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // 예외 처리 (노트가 없거나 오류가 발생한 경우)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 노트 저장을 위한 POST 메서드
     *
     * @param diaryNum        다이어리 번호
     * @param noteNum         노트 번호
     * @param noteName        노트 이름
     * @param diaryDate       작성 날짜
     * @param diaryEmotion    감정 선택
     * @param locationTag     위치 정보
     * @param diaryContent    일기 내용
     * @param hashtags        해시태그 선택
     * @param model           모델 객체
     * @param principal       인증된 사용자 정보
     * @return 저장 결과에 따른 뷰 이름
     */
    @PostMapping("/saveNote")
    public String saveNote(@RequestParam("diaryNum") Integer diaryNum,
                           @RequestParam("noteNum") Integer noteNum,
                           @RequestParam("noteName") String noteName,
                           @RequestParam("diaryDate") String diaryDate,
                           @RequestParam("diaryEmotion") Integer diaryEmotion,
                           @RequestParam("locationTag") String locationTag,
                           @RequestParam("diaryContent") String diaryContent,
                           @RequestParam(value = "hashtags", required = false) List<Integer> hashtags,
                           @RequestParam(value = "diaryImage", required = false) MultipartFile diaryImage,
                           Model model, Principal principal) {

        try {
            // NoteTemplateDTO 생성
            NoteTemplateDTO noteTemplateDTO = NoteTemplateDTO.builder()
                    .noteNum(noteNum)
                    .build();

            // 현재 로그인된 사용자 ID
            String memberId = principal.getName();


            // ShareNoteDTO 생성
            ShareNoteDTO noteDTO = ShareNoteDTO.builder()
                    .shareDiaryNum(diaryNum)
                    .noteTemplate(noteTemplateDTO)
                    .shareNoteTitle(noteName)
                    .diaryDate(Timestamp.valueOf(LocalDate.parse(diaryDate).atStartOfDay()))
                    .emotionNum(diaryEmotion) // Emotion 번호를 전달
                    .location(locationTag)
                    .contents(diaryContent)
                    .memberId(memberId)
                    .likeCount(0)
                    .weather("Sunny")
                    .build();

            // 이미지 파일 처리
            if (diaryImage != null && !diaryImage.isEmpty()) {
                // 업로드된 파일 정보 로그 출력
                log.info("업로드된 파일 이름: {}", diaryImage.getOriginalFilename());
                log.info("파일 크기: {}", diaryImage.getSize());

                // 고유한 파일 이름 생성
                String fileName = UUID.randomUUID().toString() + "_" + diaryImage.getOriginalFilename();

                // 업로드 디렉토리 경로 설정 및 생성
                File uploadDir = new File(this.uploadDir);
                if (!uploadDir.exists()) {
                    boolean dirCreated = uploadDir.mkdirs(); // 디렉토리 생성
                    log.info("업로드 디렉토리 생성 여부: {}", dirCreated);
                }

                // 파일 저장 경로 설정
                File saveFile = new File(uploadDir, fileName);
                log.info("저장될 파일 경로: {}", saveFile.getAbsolutePath());

                // 파일 저장
                diaryImage.transferTo(saveFile);
                noteDTO.setFileName(fileName); // 파일 이름 설정
            }

            // 서비스 호출
            shareNoteService.saveNote(noteDTO, hashtags);

            // 다이어리가 사용자가 생성한 것인지 확인
            String loginedMemberId = principal.getName();
            boolean isCreatedDiary = shareDiaryService.isDiaryCreatedByUser(diaryNum, loginedMemberId);

            // 다이어리 타입에 따라 리다이렉션 경로 설정
            if (isCreatedDiary) {
                return "redirect:/share/createdDiary?diaryNum=" + diaryNum;
            } else {
                return "redirect:/share/joinedDiary?diaryNum=" + diaryNum;
            }


        } catch (Exception e) {
            log.error("노트 저장 중 오류 발생", e);
            model.addAttribute("errorMessage", "노트 저장에 실패했습니다.");
            return createDiary(noteNum, diaryNum, noteName, model, principal);
        }
    }

    /**
     * 전체 공유 다이어리 리스트를 출력하는 메서드
     *
     * @param user
     * @param model
     * @return
     */
    @GetMapping("listAll")
    public String listAll(@AuthenticationPrincipal AuthenticatedUser user, Model model) {

        List<ShareDiaryDTO> diaryList = shareDiaryService.getDiaryList();
        List<String> categoryNames = shareDiaryService.getUserCategories(user);

        model.addAttribute("diaryList", diaryList);
        model.addAttribute("categoryNames", categoryNames);
        model.addAttribute("activeCategory", "전체");  // 전체 탭이 활성화되도록 설정

        log.debug("가져온 다이어리 리스트들 보여줘:{}", diaryList);
        log.debug("가져온 유저별 카테고리:{}", categoryNames);

        return "share/listAll";
    }

    /**
     * 유저별 관심 카테고리 클릭 시 해당 카테고리를 가진 다이어리를 출력하는 메서드
     * @param user
     * @param model
     * @param categoryName
     * @return
     */
    @GetMapping("listedByUserCategory")
    public String listedByUserCategory(@AuthenticationPrincipal AuthenticatedUser user, Model model,
                                       @RequestParam("categoryName") String categoryName) {

        log.debug("가져온 카테고리 이름:{}", categoryName);

        List<String> categoryNames = shareDiaryService.getUserCategories(user);
        List<ShareDiaryDTO> categoryList = shareDiaryService.getCategoryList(categoryName);

        model.addAttribute(user);
        model.addAttribute("categoryNames", categoryNames);
        model.addAttribute("diaryList", categoryList);
        model.addAttribute("activeCategory", categoryName);  // 선택된 카테고리를 active로 설정

        return "share/listAll";
    }

    /**
     * 좋아요 버튼 클릭 시 처리 메서드
     * @param noteNum
     * @param user
     * @return
     */
    @ResponseBody
    @PostMapping("/like")
    public LikeResponseDTO like(@RequestParam("noteNum") Integer noteNum,
                                @RequestParam("emotionNum") Integer emotionNum,
                                @AuthenticationPrincipal AuthenticatedUser user) {

        // 좋아요 서비스 호출
        LikeResponseDTO dto = shareNoteService.like(noteNum, emotionNum, user.getUsername());
        log.debug("보낼 dto: {}", dto);

        return dto;
    }


}
