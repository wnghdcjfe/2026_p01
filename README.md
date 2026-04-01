# Talent Hub

`prd.md`를 기반으로 구축한 Spring Boot + Thymeleaf 기반 다국어 채용/브랜딩 사이트입니다.

## 기술 스택

- Java 11 target
- Spring Boot 2.7.x
- Spring MVC
- Thymeleaf
- MessageSource 기반 i18n
- 정적 CSS / JS

## 실행 방법

```bash
./mvnw spring-boot:run
```

기본 확인 경로:

- `http://localhost:8080/ko/home`
- `http://localhost:8080/en/home`

## 검증 명령어

```bash
./mvnw test
./mvnw package
```

## 구현 범위

- 홈
- Company: 뉴스룸 / 스토리 / 캠퍼스
- Job: 직무 소개 목록/상세, 직무 인터뷰 목록/상세
- Culture: 일하는 문화 / 복지 제도
- Apply: 월간 hy-way / 채용 공고 / 채용 절차 / FAQ
- 공통 헤더 / 푸터 / 언어 전환 / 모바일 메뉴
- FAQ 아코디언 + 검색 UI
- SEO 메타 / canonical / hreflang

## 주요 URL

- `/{lang}/home`
- `/{lang}/company/newsroom`
- `/{lang}/company/story`
- `/{lang}/company/campus`
- `/{lang}/job/intro`
- `/{lang}/job/intro/{slug}`
- `/{lang}/job/interview`
- `/{lang}/job/interview/{slug}`
- `/{lang}/culture/working`
- `/{lang}/culture/benefits`
- `/{lang}/apply/hy-way`
- `/{lang}/apply/openings`
- `/{lang}/apply/process`
- `/{lang}/apply/faq`

지원 언어:

- `ko`
- `en`

지원하지 않는 언어 또는 `/` 접근 시 기본적으로 `ko` 경로로 리다이렉트됩니다.

## 프로젝트 구조

```text
src/main/java/com/talenthub
├── config      # locale/path routing 설정
├── model       # 화면용 DTO
├── service     # 콘텐츠 구성 및 trusted i18n 처리
└── web         # MVC controller

src/main/resources
├── messages_ko.properties
├── messages_en.properties
├── static
│   ├── css/site.css
│   └── js/site.js
└── templates
    ├── layout
    ├── fragments
    ├── company
    ├── job
    ├── culture
    └── apply

scripts
└── messages-excel.js   # properties ↔ Excel용 CSV 변환

i18n
└── messages.csv        # Excel에서 바로 열 수 있는 UTF-8 BOM CSV
```

## 다국어 / HTML 번역 정책

이 프로젝트는 PRD 요구사항에 맞춰 **문장 완성형 번역**을 사용합니다.

예:

```properties
home.hero.titleHtml=반도체와 디지털 혁신이 만나는 <br/><strong>당신의 다음 커리어</strong>
```

핵심 원칙:

- 언어별 문장은 각 언어가 직접 소유합니다.
- `<br>`, `<strong>`, `<em>`만 trusted HTML로 허용합니다.
- 메시지 리소스의 trusted 문자열만 `th:utext` 경로로 렌더링합니다.
- 사용자 입력/외부 데이터는 raw HTML로 출력하지 않습니다.

관련 구현:

- `TrustedMessageService`
- `messages_ko.properties`
- `messages_en.properties`

## Excel 기반 번역 워크플로

추가 의존성 없이 **Excel에서 바로 열 수 있는 UTF-8 BOM CSV** 방식으로 번역 파일을 관리할 수 있습니다.

### 1) properties → CSV 내보내기

```bash
node scripts/messages-excel.js export
```

생성 파일:

- `i18n/messages.csv`

CSV 컬럼:

- `group`
- `key`
- `ko`
- `en`
- `status`
- `notes`

### 2) Excel에서 편집

- `i18n/messages.csv`를 Excel로 엽니다.
- `key`, `ko`, `en` 컬럼을 기준으로 수정합니다.
- 저장 시 가능하면 **CSV UTF-8** 형식으로 저장합니다.

### 3) CSV → properties 가져오기

```bash
node scripts/messages-excel.js import
```

그러면 다음 파일이 다시 생성됩니다.

- `src/main/resources/messages_ko.properties`
- `src/main/resources/messages_en.properties`

### 커스텀 경로 예시

```bash
node scripts/messages-excel.js export --csv ./i18n/messages-for-translators.csv
node scripts/messages-excel.js import --csv ./i18n/messages-for-translators.csv
```

## 테스트 범위

- `/` → `/ko/home` 리다이렉트
- 미지원 언어 경로 → `/ko/home` 리다이렉트
- 영문 홈 trusted HTML 렌더링 확인
- 상세 페이지 언어 전환 링크 유지 확인
- FAQ 페이지 렌더링 확인
- sanitizer에서 허용 태그 유지 / 비허용 태그 escape 확인

## 확장 포인트

향후 아래 방향으로 확장할 수 있습니다.

- CMS / API / DB 연동
- 게시판형 뉴스/스토리 관리
- 검색 / 필터 / 페이징 백엔드
- 관리자 화면
- 운영용 콘텐츠 관리 워크플로
