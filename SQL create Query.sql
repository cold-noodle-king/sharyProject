-- 1. 카테고리 테이블
CREATE TABLE category
(
    category_num  INT AUTO_INCREMENT PRIMARY KEY, -- 카테고리 번호 (PK)
    category_name VARCHAR(30) NOT NULL            -- 카테고리 이름
);

-- 2. 채팅 테이블
CREATE TABLE chat
(
    chat_id         INT AUTO_INCREMENT PRIMARY KEY,      -- 채팅 ID (PK)
    created_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성 날짜
    participant1_id VARCHAR(50) NOT NULL,                -- 참가자1 ID
    participant2_id VARCHAR(50) NOT NULL                 -- 참가자2 ID
);

-- 3. 채팅 메시지 테이블
CREATE TABLE chat_message
(
    message_id      INT AUTO_INCREMENT PRIMARY KEY,      -- 메시지 ID (PK)
    chat_id         INT         NOT NULL,                -- 채팅 ID (FK)
    message_content TEXT        NOT NULL,                -- 메시지 내용
    created_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 메시지 생성 날짜
    sender_id       VARCHAR(50) NOT NULL                 -- 발신자 ID
);

-- 4. 커버 템플릿 테이블
CREATE TABLE cover_template
(
    cover_num   INT AUTO_INCREMENT PRIMARY KEY, -- 커버 번호 (PK)
    cover_name  VARCHAR(100) NOT NULL,          -- 커버 이름
    cover_image VARCHAR(255) NOT NULL           -- 커버 이미지 파일 경로
);

-- 5. 감정 테이블
CREATE TABLE emotion
(
    emotion_num  INT AUTO_INCREMENT PRIMARY KEY, -- 감정 번호 (PK)
    emotion_name VARCHAR(30) NOT NULL            -- 감정 이름
);

-- 6. 감정 선택 여부 테이블
CREATE TABLE emotion_clicked
(
    liked_num     INT AUTO_INCREMENT PRIMARY KEY, -- 감정 선택 번호 (PK)
    like_emotions INT,                            -- 선택한 감정 번호
    member_id     VARCHAR(50) NOT NULL            -- 회원 ID (FK)
);

-- 7. 팔로우 테이블
CREATE TABLE follow
(
    follower_id  VARCHAR(255) NOT NULL,               -- 팔로워 ID
    following_id VARCHAR(255) NOT NULL,               -- 팔로잉 ID
    follow_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 팔로우 날짜
    PRIMARY KEY (follower_id, following_id)           -- 복합 키 설정
);

-- 8. 권한 테이블
CREATE TABLE granted
(
    granted_num  INT AUTO_INCREMENT PRIMARY KEY, -- 권한 번호 (PK)
    granted_name VARCHAR(30) NOT NULL            -- 권한 이름
);

-- 9. 해시태그 테이블
CREATE TABLE hashtag
(
    hashtag_num  INT AUTO_INCREMENT PRIMARY KEY, -- 해시태그 번호 (PK)
    hashtag_name VARCHAR(50),                    -- 해시태그 이름
    category_num INT NOT NULL                    -- 카테고리 번호 (FK)
);

-- 10. 회원 테이블
CREATE TABLE member
(
    member_id    VARCHAR(50)  NOT NULL PRIMARY KEY,                                           -- 회원 ID (PK)
    password     VARCHAR(100) NOT NULL,                                                       -- 비밀번호
    email        VARCHAR(100) NOT NULL,                                                       -- 이메일
    phone_number VARCHAR(20)  NOT NULL,                                                       -- 전화번호
    full_name    VARCHAR(100) NOT NULL,                                                       -- 회원 이름
    nickname     VARCHAR(50)  NOT NULL,                                                       -- 닉네임
    gender       CHAR(10)     NOT NULL,                                                       -- 성별
    birthdate    DATE         NOT NULL,                                                       -- 생년월일
    created_date TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,                             -- 생성 날짜
    updated_date TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 날짜
    role_name    VARCHAR(50)  NOT NULL DEFAULT 'ROLE_USER',                                   -- 역할 (기본값: ROLE_USER)
    enabled      TINYINT(1) DEFAULT 1                                                         -- 계정 활성화 여부
);

-- 11. 음악 테이블
CREATE TABLE music
(
    music_num   INT AUTO_INCREMENT PRIMARY KEY, -- 음악 번호 (PK)
    music_name  VARCHAR(100) NOT NULL,          -- 음악 이름
    emotion_num INT          NOT NULL,          -- 감정 번호 (FK)
    music_url   VARCHAR(255) NOT NULL,          -- 음악 URL
    artist      VARCHAR(100) NOT NULL           -- 아티스트
);

-- 12. 노트 템플릿 테이블
CREATE TABLE note_template
(
    note_num   INT AUTO_INCREMENT PRIMARY KEY, -- 노트 번호 (PK)
    note_name  VARCHAR(50)  NOT NULL,          -- 노트 이름
    note_image VARCHAR(255) NOT NULL           -- 노트 이미지 파일 경로
);

-- 13. 알림 테이블
CREATE TABLE notice
(
    notice_num     INT AUTO_INCREMENT PRIMARY KEY,      -- 알림 번호 (PK)
    notice_type    VARCHAR(100),                        -- 알림 종류
    notice_message VARCHAR(1000),                       -- 알림 메시지
    is_read        TINYINT(1) NOT NULL DEFAULT 0,       -- 읽음 여부 (기본값: 0)
    created_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성 날짜
    member_id      VARCHAR(50) NOT NULL                 -- 회원 ID (FK)
);

-- 14. 알림 설정 테이블
CREATE TABLE notice_setting
(
    setting_id INT AUTO_INCREMENT PRIMARY KEY, -- 알림 설정 번호 (PK)
    is_enabled TINYINT(1) DEFAULT 1,           -- 알림 활성화 여부
    member_id  VARCHAR(50) NOT NULL,           -- 회원 ID (FK)
    notice_num INT         NOT NULL            -- 알림 번호 (FK)
);

-- 15. 알림 내역 테이블
CREATE TABLE notifications
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,                -- 알림 내역 번호 (PK)
    receiver_id       VARCHAR(50)   NOT NULL,                           -- 수신자 ID (FK)
    content           VARCHAR(1000) NOT NULL,                           -- 알림 내용
    created_at        TIMESTAMP              DEFAULT CURRENT_TIMESTAMP, -- 생성 날짜
    is_read           TINYINT(1) NOT NULL DEFAULT 0,                    -- 읽음 여부
    notification_type VARCHAR(255)  NOT NULL DEFAULT 'system'           -- 알림 유형
);

-- 16. 개인 다이어리 테이블
CREATE TABLE personal_diary
(
    personal_diary_num INT AUTO_INCREMENT PRIMARY KEY,                                  -- 개인 다이어리 번호 (PK)
    diary_name         VARCHAR(100) NOT NULL,                                           -- 다이어리 이름
    created_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- 생성 날짜
    updated_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 날짜
    category_num       INT          NOT NULL,                                           -- 카테고리 번호 (FK)
    cover_num          INT          NOT NULL,                                           -- 커버 번호 (FK)
    member_id          VARCHAR(50)  NOT NULL                                            -- 회원 ID (FK)
);

-- 17. 개인 다이어리 좋아요 테이블
CREATE TABLE personal_like
(
    personal_like_num INT AUTO_INCREMENT PRIMARY KEY, -- 개인 다이어리 좋아요 번호 (PK)
    personal_note_num INT         NOT NULL,           -- 개인 노트 번호 (FK)
    member_id         VARCHAR(50) NOT NULL,           -- 회원 ID (FK)
    like_clicked      TINYINT(1) DEFAULT 0            -- 좋아요 클릭 여부
);

-- 18. 개인 노트 테이블
CREATE TABLE personal_note
(
    personal_note_num  INT AUTO_INCREMENT PRIMARY KEY,                                  -- 개인 노트 번호 (PK)
    note_title         VARCHAR(100) NOT NULL,                                           -- 노트 제목
    weather            VARCHAR(100) NOT NULL,                                           -- 날씨 정보
    original_name      VARCHAR(500),                                                    -- 원본 파일 이름
    file_name          VARCHAR(500),                                                    -- 저장된 파일 이름
    location           VARCHAR(500),                                                    -- 위치 정보
    note_num           INT          NOT NULL,                                           -- 노트 번호 (FK)
    emotion_num        INT          NOT NULL,                                           -- 감정 번호 (FK)
    personal_diary_num INT          NOT NULL,                                           -- 개인 다이어리 번호 (FK)
    profile_num        INT          NOT NULL,                                           -- 프로필 번호 (FK)
    member_id          VARCHAR(50)  NOT NULL,                                           -- 회원 ID (FK)
    contents           TEXT,                                                            -- 노트 내용
    diary_date         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- 다이어리 날짜
    created_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- 생성 날짜
    updated_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 날짜
    granted_num        INT          NOT NULL,                                           -- 권한 번호 (FK)
    like_count         INT       DEFAULT 0,                                             -- 좋아요 수
    view_count         INT       DEFAULT 0                                              -- 조회 수
);

-- 19. 개인 노트 해시태그 테이블
CREATE TABLE personal_note_hashtag
(
    personal_note_num INT NOT NULL,              -- 개인 노트 번호 (FK)
    hashtag_num       INT NOT NULL,              -- 해시태그 번호 (FK)
    PRIMARY KEY (personal_note_num, hashtag_num) -- 복합 키 설정
);

-- 20. 개인 다이어리 재생 테이블
CREATE TABLE personal_play
(
    personal_play_num INT AUTO_INCREMENT PRIMARY KEY, -- 개인 다이어리 재생 번호 (PK)
    personal_note_num INT NOT NULL,                   -- 개인 노트 번호 (FK)
    music_num         INT NOT NULL,                   -- 음악 번호 (FK)
    playing           TINYINT(1) NOT NULL             -- 재생 여부
);

-- 21. 프로필 테이블
CREATE TABLE profile
(
    profile_num           INT AUTO_INCREMENT PRIMARY KEY, -- 프로필 번호 (PK)
    profile_picture       VARCHAR(100) NOT NULL,          -- 프로필 사진
    ment                  VARCHAR(300),                   -- 소개글
    member_id             VARCHAR(50)  NOT NULL,          -- 회원 ID (FK)
    profile_original_name VARCHAR(255)                    -- 프로필 원본 파일 이름
);

-- 22. 댓글 테이블
CREATE TABLE reply
(
    reply_num      INT AUTO_INCREMENT PRIMARY KEY,                                  -- 댓글 번호 (PK)
    contents       VARCHAR(2000) NOT NULL,                                          -- 댓글 내용
    created_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- 생성 날짜
    updated_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 날짜
    share_note_num INT           NOT NULL,                                          -- 공유 노트 번호 (FK)
    member_id      VARCHAR(50)   NOT NULL                                           -- 회원 ID (FK)
);

-- 23. 공유 다이어리 테이블
CREATE TABLE share_diary
(
    share_diary_num  INT AUTO_INCREMENT PRIMARY KEY,                                  -- 공유 다이어리 번호 (PK)
    share_diary_name VARCHAR(100) NOT NULL,                                           -- 공유 다이어리 이름
    created_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- 생성 날짜
    updated_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 날짜
    category_num     INT          NOT NULL,                                           -- 카테고리 번호 (FK)
    cover_num        INT          NOT NULL,                                           -- 커버 번호 (FK)
    member_id        VARCHAR(50)  NOT NULL,                                           -- 회원 ID (FK)
    diary_bio        VARCHAR(255)                                                     -- 다이어리 소개글
);

-- 24. 공유 다이어리 좋아요 테이블
CREATE TABLE share_like
(
    like_num       INT AUTO_INCREMENT PRIMARY KEY, -- 공유 다이어리 좋아요 번호 (PK)
    share_note_num INT         NOT NULL,           -- 공유 노트 번호 (FK)
    member_id      VARCHAR(50) NOT NULL,           -- 회원 ID (FK)
    like_clicked   TINYINT(1) DEFAULT 0            -- 좋아요 클릭 여부
);

-- 25. 공유 다이어리 멤버 테이블
CREATE TABLE share_member
(
    share_member_num INT AUTO_INCREMENT PRIMARY KEY,                -- 공유 다이어리 멤버 번호 (PK)
    member_id        VARCHAR(50) NOT NULL,                          -- 회원 ID (FK)
    share_diary_num  INT         NOT NULL,                          -- 공유 다이어리 번호 (FK)
    manager_id       VARCHAR(50) NOT NULL,                          -- 관리자 ID (FK)
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',        -- 상태 (기본값: PENDING)
    join_date        TIMESTAMP            DEFAULT CURRENT_TIMESTAMP -- 가입 날짜
);

-- 26. 공유 노트 테이블
CREATE TABLE share_note
(
    share_note_num   INT AUTO_INCREMENT PRIMARY KEY,                                  -- 공유 노트 번호 (PK)
    share_note_title VARCHAR(100) NOT NULL,                                           -- 공유 노트 제목
    weather          VARCHAR(100) NOT NULL,                                           -- 날씨 정보
    original_name    VARCHAR(500),                                                    -- 원본 파일 이름
    file_name        VARCHAR(500),                                                    -- 저장된 파일 이름
    location         VARCHAR(500),                                                    -- 위치 정보
    note_num         INT          NOT NULL,                                           -- 노트 번호 (FK)
    emotion_num      INT          NOT NULL,                                           -- 감정 번호 (FK)
    profile_num      INT          NOT NULL,                                           -- 프로필 번호 (FK)
    member_id        VARCHAR(50)  NOT NULL,                                           -- 회원 ID (FK)
    contents         TEXT,                                                            -- 노트 내용
    diary_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- 다이어리 날짜
    created_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                             -- 생성 날짜
    updated_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 날짜
    like_count       INT       DEFAULT 0,                                             -- 좋아요 수
    share_diary_num  INT          NOT NULL                                            -- 공유 다이어리 번호 (FK)
);

-- 27. 공유 노트 해시태그 테이블
CREATE TABLE share_note_hashtag
(
    share_note_num INT NOT NULL,              -- 공유 노트 번호 (FK)
    hashtag_num    INT NOT NULL,              -- 해시태그 번호 (FK)
    PRIMARY KEY (share_note_num, hashtag_num) -- 복합 키 설정
);

-- 28. 공유 다이어리 재생 테이블
CREATE TABLE share_play
(
    share_play_num INT AUTO_INCREMENT PRIMARY KEY, -- 공유 다이어리 재생 번호 (PK)
    music_num      INT NOT NULL,                   -- 음악 번호 (FK)
    share_note_num INT NOT NULL,                   -- 공유 노트 번호 (FK)
    playing        TINYINT(1) NOT NULL             -- 재생 여부
);

-- 29. 실시간 메시지 테이블
CREATE TABLE sse_message
(
    message_id  INT AUTO_INCREMENT PRIMARY KEY,      -- 메시지 ID (PK)
    from_id     VARCHAR(50)   NOT NULL,              -- 발신자 ID
    to_id       VARCHAR(50)   NOT NULL,              -- 수신자 ID
    content     VARCHAR(1000) NOT NULL,              -- 메시지 내용
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성 날짜
    is_read     TINYINT(1) DEFAULT 0                 -- 읽음 여부
);

-- 30. 사용자 카테고리 테이블
CREATE TABLE user_category
(
    user_category_num INT AUTO_INCREMENT PRIMARY KEY, -- 사용자 카테고리 번호 (PK)
    member_id         VARCHAR(50) NOT NULL,           -- 회원 ID (FK)
    category_num      INT         NOT NULL            -- 카테고리 번호 (FK)
);