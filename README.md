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

## 로컬 개발 초기 설정 가이드

아래 순서대로 진행하면 JDK 11 설치부터 IntelliJ 실행까지 한 번에 맞출 수 있습니다.

### 1) JDK 11 설치

이 프로젝트는 **JDK 11 기준**으로 맞춰져 있습니다.

확인:

```bash
java -version
```

정상 예시:

```text
openjdk version "11.x.x"
```

#### macOS

1. Temurin / Oracle JDK / Amazon Corretto 등 **JDK 11 배포판** 중 하나를 설치합니다.
2. 설치 후 터미널에서 아래처럼 JDK 11이 잡히는지 확인합니다.

```bash
java -version
/usr/libexec/java_home -V
```

3. 여러 JDK가 설치되어 있다면 필요 시 `JAVA_HOME`을 11로 맞춥니다.

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
```

#### Windows

1. JDK 11 설치 파일(msi/exe)로 설치합니다.
2. `JAVA_HOME`을 JDK 11 설치 경로로 지정합니다.
3. `Path`에 `%JAVA_HOME%\\bin`을 추가합니다.
4. 새 터미널에서 확인합니다.

```bat
java -version
echo %JAVA_HOME%
```

#### Ubuntu / Debian 계열

```bash
sudo apt update
sudo apt install openjdk-11-jdk
java -version
```

### 2) 프로젝트 최초 실행

저장소 루트에서 아래 명령으로 확인합니다.

```bash
./mvnw test
./mvnw spring-boot:run
```

접속:

- `http://localhost:8080/ko/home`
- `http://localhost:8080/en/home`

### 3) IntelliJ IDEA 초기 설정

#### 프로젝트 열기

1. IntelliJ IDEA 실행
2. **Open** 선택
3. 현재 저장소 루트(`2026_p01`)를 선택
4. Maven 프로젝트로 인식되면 import/sync 진행

#### Project SDK 설정

1. `File` → `Project Structure`
2. `Project` 탭에서:
   - **Project SDK** = 설치한 **JDK 11**
   - **Project language level** = `11` 또는 `SDK default`

#### Maven 설정

1. `Settings` → `Build, Execution, Deployment` → `Build Tools` → `Maven`
2. 가능하면 다음 기준으로 맞춥니다.
   - **JDK for importer** = `Project SDK (JDK 11)`
   - **Runner JDK** = `Project SDK (JDK 11)`
3. Maven 변경 사항이 있으면 Reload 합니다.

#### 인코딩 설정

한글 메시지 파일이 있으므로 UTF-8로 맞추는 것이 안전합니다.

1. `Settings` → `Editor` → `File Encodings`
2. 아래 항목을 모두 `UTF-8`로 설정 권장
   - Global Encoding
   - Project Encoding
   - Default encoding for properties files

### 4) IntelliJ에서 실행하기

#### 방법 A. Spring Boot 메인 클래스 실행

1. `src/main/java/com/talenthub/TalentHubApplication.java` 열기
2. Run 아이콘 클릭

#### 방법 B. Maven Task로 실행

IntelliJ Maven 창에서:

- `test`
- `spring-boot:run`

### 5) IntelliJ에서 처음 보면 체크할 항목

- JDK가 11이 아닌 17/21 등으로 잡혀 있지 않은지
- Maven Import JDK도 11인지
- `messages_ko.properties`, `messages_en.properties` 인코딩이 UTF-8인지
- 실행 후 `/ko/home`, `/en/home` 둘 다 열리는지

### 6) 자주 겪는 문제

#### `java version`이 11이 아님

- 터미널의 `JAVA_HOME`이 다른 버전을 가리킬 수 있습니다.
- IntelliJ Project SDK와 터미널 JDK를 둘 다 확인하세요.

#### IntelliJ에서 Maven sync 실패

- Maven Import JDK를 11로 다시 지정
- 우측 Maven 패널에서 Reload
- 필요 시 `.idea` 삭제 후 다시 Open

#### 한글이 깨져 보임

- IntelliJ 파일 인코딩을 UTF-8로 변경
- Excel 번역 파일은 가능하면 **CSV UTF-8**로 저장

#### 실행은 되는데 페이지가 안 보임

- 먼저 아래 명령으로 기본 검증

```bash
./mvnw test
./mvnw spring-boot:run
```

- 그 다음 브라우저에서 `/ko/home` 경로로 직접 접속해 확인합니다.
