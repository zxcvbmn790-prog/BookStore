# BOOK STORE · 온라인 서점 플랫폼

> Spring MVC 기반 온라인 서점 — 도서 탐색·구매부터 실시간 상담, 회원 등급·마일리지, 관리자 통계까지 하나로 구현한 웹 애플리케이션

`Java 21` · `Spring MVC` · `Spring Security` · `JSP` · `WebSocket` · `H2 / Oracle`

---

## 프로젝트 소개

- **목적**: 도서 탐색·검색·구매, 고객 상담(실시간 채팅·QnA), 회원 등급/마일리지, 관리자 운영을 아우르는 온라인 서점 플랫폼
- **아키텍처**: 표준 Spring MVC 계층 구조 — **Controller → Service → DAO → VO**
- **기본 계정**: 최초 기동 시 자동 생성 — 관리자 `admin / 1234`, 일반 `user / 1234`

---

## 주요 기능

### 사용자 (User)
-  **회원가입·로그인** — Email OTP 인증(Gmail SMTP, 6자리·3분 유효) + Google reCAPTCHA v2, 아이디 중복 확인
-  **카카오 소셜 로그인** — Kakao OAuth2 로그인 및 자동 회원가입
-  **도서 탐색** — 목록(무한 스크롤)·상세·검색, 인기 검색어, 좋아요·평점 등록/취소
-  **장바구니** — 비회원도 쿠키 기반으로 담기(7일 보관), 로그인 시 주문으로 이관
-  **주문·결제** — 주문/결제·주문내역, 첫 주문 시 배송지 자동 저장 → 재주문 시 자동 입력
-  **회원 등급·마일리지** — BRONZE(1%) → SILVER(2%) → GOLD(3%) → PLATINUM(5%), 누적 마일리지 기준 자동 승급 및 적립·사용
-  **실시간 1:1 상담** — WebSocket 기반 사용자–관리자 채팅(이력 저장), QnA·FAQ
-  **마이페이지** — 프로필 관리, 회원 탈퇴(비밀번호 확인)

### 관리자 (Manager)
-  **도서 관리(CRUD)** — 카카오 ISBN 자동조회로 제목·저자·출판사·표지·가격 자동 입력
-  **판매 통계 대시보드** — 일·주·월·연 차트, 인기 도서 TOP5, 실시간 매출
-  **회원 관리** — 회원 검색, 등급·할인율 관리
-  **광고 배너 관리** — 배너 노출도(0~99%) 설정
-  **배송 관리** — 배송 상태 관리 (`ROLE_TRACKING` 별도 권한)
-  **권한 분리** — `ROLE_ADMIN`(도서·통계·회원) / `ROLE_TRACKING`(배송)

---

## 기술 스택 (Tech Stack)

**Backend**
- **Language** : Java 21
- **Framework** : Spring MVC, Spring JDBC (JdbcTemplate)
- **Security** : Spring Security (인증·인가, reCAPTCHA 연동)
- **Realtime** : Spring WebSocket
- **Library** : Lombok, Jackson, OpenCSV, Apache Commons(FileUpload·IO·Text·Codec), JavaMail

**Frontend**
- **View** : JSP, JSTL
- **Web** : HTML · CSS · JavaScript(AJAX)

**Database**
- H2 Database (로컬 개발) / Oracle `ojdbc11` (운영)

**외부 연동**
- Kakao API (도서 ISBN 조회 · OAuth2 로그인)
- Google reCAPTCHA v2
- Gmail SMTP (OTP 메일 발송)

**빌드 · 실행**
- Maven (WAR 패키징) · Apache Tomcat 9+

---

## 프로젝트 구조

```
BookStore/
├── src/main/
│   ├── java/WebBookStore/
│   │   ├── admin/      # 관리자 · 판매통계 · 카카오 ISBN · 배송관리
│   │   ├── book/       # 도서 목록·상세·검색·평점·좋아요 · CSV 초기화
│   │   ├── cart/       # 장바구니 (비회원 쿠키)
│   │   ├── chat/       # WebSocket 실시간 1:1 채팅
│   │   ├── common/     # 메인 · DB 초기화(DatabaseInitializer)
│   │   ├── member/     # 회원 · OTP · 카카오 로그인 · 마이페이지
│   │   ├── order/      # 주문 · 결제 · 주문내역
│   │   ├── qna/        # QnA · FAQ
│   │   ├── search/     # 인기 검색어
│   │   └── support/    # 고객 지원
│   ├── resources/data/books.csv       # 초기 도서 데이터
│   └── webapp/WEB-INF/
│       ├── views/                     # JSP 뷰 (기능별 폴더)
│       ├── web.xml
│       └── dispatcher-servlet.xml     # Spring MVC + Security 설정
├── DB_patch.sql                       # 보조 SQL 스크립트
└── pom.xml
```

---

## 빌드 및 실행

```bash
# 1) WAR 빌드
mvn clean package

# 2) 실행 (둘 중 택1)
mvn tomcat7:run            # 내장 플러그인, http://localhost:8080
#  또는 생성된 WAR를 Apache Tomcat 9+ 에 배포
```

- 최초 기동 시 **`DatabaseInitializer`** 가 테이블(member·book·cart·orders·chat_message 등)을 생성하고 기본 계정(`admin/1234`, `user/1234`)을 넣습니다.
- **`BookCsvInitializer`** 가 `src/main/resources/data/books.csv` 로 도서 데이터를 적재합니다.
- DB 접속 정보는 `dispatcher-servlet.xml` 에 정의됩니다 (기본 H2: `jdbc:h2:tcp://localhost/~/test`).

---

## 주요 엔드포인트

**Member**

| Method | URL | 설명 |
| --- | --- | --- |
| GET / POST | `/member/login` | 로그인 (AJAX 로그인 지원) |
| GET | `/member/register` | 회원가입 |
| GET | `/member/checkId` | 아이디 중복 확인 |
| POST | `/member/sendOtp` | OTP 발송 (+ reCAPTCHA) |
| POST | `/member/verifyOtp` | OTP 검증 |
| GET | `/member/kakaoStart` | 카카오 로그인 |
| GET | `/member/profile` | 마이페이지 |
| POST | `/member/withdraw` | 회원 탈퇴 |

**Book / Search**

| Method | URL | 설명 |
| --- | --- | --- |
| GET | `/book/list` · `/book/list/ajax` | 도서 목록 · 무한 스크롤 |
| GET | `/book/view?isbn=` | 도서 상세 |
| GET | `/book/search` | 도서 검색 |
| GET | `/book/ads` | 광고 배너 노출 |
| POST | `/book/like` · `/book/rate` · `/book/rate/cancel` | 좋아요 · 평점 등록/취소 |
| GET | `/search/popular` | 인기 검색어 |

**Cart & Order**

| Method | URL | 설명 |
| --- | --- | --- |
| GET | `/cart/list` | 장바구니 |
| POST | `/cart/insert` · `/cart/update` · `/cart/delete` | 담기 · 수량 변경 · 삭제 |
| GET | `/order/checkout` | 주문 / 결제 |
| POST | `/order/pay` | 결제 처리 |
| GET | `/order/list` | 주문 내역 |

**Admin** (`ROLE_ADMIN`)

| Method | URL | 설명 |
| --- | --- | --- |
| GET | `/admin/insertform` · `/admin/updateform` | 도서 등록 · 수정 |
| GET | `/admin/kakaoBookSearch` | ISBN 자동조회 |
| GET | `/admin/sales` | 판매 통계 |
| GET | `/admin/members` | 회원 관리 |
| POST | `/admin/updateDiscount` · `/admin/updateAd` | 할인율 · 광고 설정 |

**Tracking** (`ROLE_TRACKING`)

| Method | URL | 설명 |
| --- | --- | --- |
| GET | `/admin/traking` | 배송 관리 |
| POST | `/admin/updateTracking` | 배송 상태 변경 |

---

<p align="center"><b>📖 BOOK STORE — Team Project </b></p>
