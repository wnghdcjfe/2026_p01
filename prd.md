# PRD 초안

## 1. 프로젝트 개요

SK hynix Talent Hub 스타일의 채용/브랜드 사이트를 스프링부트와 타임리프로 재구현한다.
1차 목표는 정적/반정적 콘텐츠 중심의 채용 브랜딩 사이트 구현이며, 2차 목표는 ko/en 전환이 가능한 완전한 다국어 구조를 제공하는 것이다.
특히 번역 문자열 내부에 `<br>` 같은 HTML 태그가 포함되는 문구도 언어별 어순 차이를 반영하여 자연스럽게 표현할 수 있어야 한다.

“다국어 문구는 키 단위 단순 치환이 아니라 언어별 완성 문장 단위로 관리하며, `<br>` 등 제한된 인라인 HTML은 메시지 리소스에서 허용하고 Thymeleaf의 `th:utext`로 렌더링한다. 단, 해당 방식은 운영자가 관리하는 신뢰 가능한 메시지 리소스에만 적용하고, 사용자 입력 또는 외부 데이터는 sanitize 없이 HTML 출력하지 않는다.”
 

## 2. 목표

* Talent Hub와 유사한 정보 구조를 가진 채용 브랜딩 사이트 구현
* JDK 11 + Spring Boot + Thymeleaf 기반 서버사이드 렌더링
* ko / en 다국어 지원
* 언어별 URL 또는 Locale 기반 페이지 제공
* 번역문 내부의 `<br>`, `<strong>` 등 제한된 인라인 태그 표현 지원
* SEO 및 접근성 기본 대응
* 추후 CMS/API 연동 가능한 구조로 설계

## 3. 사용자 유형

* 채용 지원자
* 직무 탐색 사용자
* 기업 문화 탐색 사용자
* 글로벌 지원자(영문 사용자)
* 운영 관리자(향후 확장)

## 4. 핵심 메뉴 구조

현재 Talent Hub 기준 핵심 정보 구조는 다음과 같이 잡는다. ([SK hynix Talent Hub][1])

* Company

  * 뉴스룸
  * 스토리
  * 캠퍼스
* Job

  * 직무 소개
  * 직무 인터뷰
* Culture

  * 일하는 문화
  * 복지 제도
* Apply

  * 월간 hy-way
  * 채용 공고
  * 채용 절차
  * 자주 묻는 질문

## 5. 페이지 범위

1차 구현 범위

* 홈
* 직무 소개 목록/상세
* 직무 인터뷰 목록/상세
* 일하는 문화
* 복지 제도
* 채용 절차
* FAQ
* 공통 헤더/푸터
* ko/en 언어 전환

선택 구현 범위

* 뉴스/스토리/캠퍼스 게시판형 페이지
* 검색
* 필터
* 페이징
* 관리자 연동용 데이터 구조

## 6. 기능 요구사항

### 6.1 공통

* 반응형 레이아웃
* 헤더 글로벌 네비게이션
* 모바일 메뉴
* 푸터
* 현재 언어 상태 표시
* ko/en 언어 전환 버튼
* 공통 메타 태그 출력

### 6.2 홈

* 메인 비주얼
* 대표 섹션 카드
* 추천 콘텐츠
* 채용 공고/채용 절차 진입 링크
* 직무 인터뷰/문화 콘텐츠 하이라이트

### 6.3 채용 절차

실제 Talent Hub처럼 서류전형 → SKCT/A!SK → 면접전형 → 최종 합격 및 입사 흐름을 단계형 UI로 노출한다. ([SK hynix Talent Hub][2])

### 6.4 문화 페이지

실제 사이트의 핵심 문화 키워드 구조처럼 가치 키워드 + 설명 + 세부 항목 구조를 갖는다. 예: Bar Raising, Data Driven, One Team 등. ([SK hynix Talent Hub][3])

### 6.5 FAQ

* 카테고리별 아코디언
* 검색 가능 구조 고려

## 7. 비기능 요구사항

* JDK 11 호환
* Spring Boot 2.x 또는 JDK11 지원 가능한 3.x 범위 검토 후 확정
* SSR 중심으로 초기 로딩 최적화
* XSS 방지
* 다국어 메시지 누락 시 fallback 제공
* 접근성: 이미지 alt, 버튼 label, 키보드 접근성
* SEO: title, description, canonical, hreflang

## 8. 기술 스택

* JDK 11
* Spring Boot
* Thymeleaf
* Spring MVC
* MessageSource 기반 i18n
* Bean Validation
* 필요 시 Lombok
* 정적 리소스: CSS, JS
* 향후 확장

  * JPA
  * MyBatis
  * CMS/API 연동

## 9. URL 정책

가장 권장하는 방식은 언어가 URL에 명시되는 구조다.

예시:

* `/ko/home`
* `/en/home`
* `/ko/job/intro`
* `/en/job/intro`

이 방식이 좋은 이유는

* SEO에 유리함
* 공유 링크가 명확함
* 서버 렌더링에서 locale 판별이 단순함

대안으로 `?lang=ko`, `?lang=en`도 가능하지만, PRD 단계에서는 path locale 방식을 추천한다.

## 10. 국제화(i18n) 설계

### 10.1 메시지 파일 구조

```properties
messages_ko.properties
messages_en.properties
```

예시:

```properties
nav.company=Company
nav.job=Job
nav.culture=Culture
nav.apply=Apply

home.hero.title=당신의 가능성이 시작되는 곳
home.hero.desc=미래를 함께 설계할 인재를 기다립니다.
```

영문:

```properties
nav.company=Company
nav.job=Job
nav.culture=Culture
nav.apply=Apply

home.hero.title=Where Your Potential Begins
home.hero.desc=We are looking for talents who will shape the future with us.
```

### 10.2 Locale 처리 방식

* `LocaleResolver` 또는 `LocaleContextResolver` 설정
* 인터셉터로 URL path, 파라미터, 쿠키 중 하나를 기준으로 locale 결정
* 기본 locale은 `ko`
* 미지원 언어 접근 시 `ko` fallback

## 11. HTML 태그 포함 번역 처리 규칙

이 부분이 핵심이다.

사용자가 말한
`나는 너를 <br> 사랑해`
`I love <br> you`
같은 문구는 언어별 어순이 다르기 때문에, 단순 문자열 치환으로 처리하면 안 되고 번역문 자체가 줄바꿈 위치를 소유해야 한다.

즉, 아래처럼 메시지 파일에 언어별 완성 문장을 각각 둔다.

```properties
hero.message=나는 너를 <br/> 사랑해
```

```properties
hero.message=I love <br/> you
```

타임리프에서는 일반 텍스트 출력이 아니라 HTML 허용 출력으로 렌더링한다.

```html
<p th:utext="#{hero.message}"></p>
```

이렇게 하면 언어별로 `<br/>` 위치가 달라도 자연스럽게 나온다.

다만 이 방식은 아무 HTML이나 허용하면 위험하므로 규칙을 명확히 둬야 한다.

### 11.1 허용 규칙

메시지 리소스에서 허용하는 태그는 제한한다.

허용 예:

* `<br>`
* `<br/>`
* `<strong>`
* `<em>`
* `<span class='highlight'>`는 원칙적으로 비권장, 꼭 필요할 때만

비허용 예:

* `<script>`
* 이벤트 속성 포함 태그
* 임의 스타일 삽입
* 외부 링크 삽입

### 11.2 출력 원칙

* 운영자가 관리하는 정적 메시지 파일만 `th:utext` 허용
* 사용자 입력값이나 DB 자유입력값은 절대 그대로 `th:utext` 하지 않음
* DB 연동이 들어가면 서버단 sanitize 후 허용 태그만 남긴 뒤 출력

## 12. 더 안전한 번역 패턴

문장 전체에 태그를 직접 넣는 방식도 가능하지만, 유지보수 관점에서는 두 가지 레벨로 나누는 것이 좋다.

### 방식 A. 완성 문장 번역

```properties
hero.message=나는 너를 <br/> 사랑해
hero.message=I love <br/> you
```

장점:

* 번역가가 문장 완성형으로 관리 가능
* 어순 차이 대응이 가장 쉬움

단점:

* HTML 태그가 메시지 파일에 직접 들어감

### 방식 B. 자리표시자 패턴

```properties
hero.message={0}<br/>{1}
```

ko:

* arg0 = 나는 너를
* arg1 = 사랑해

en:

* arg0 = I love
* arg1 = you

장점:

* 일부 재사용 가능

단점:

* 실제 번역에서는 문장 전체를 번역가가 잡는 편이 더 자연스러움

이 프로젝트에서는 문장 자연스러움이 더 중요하므로, PRD에는 방식 A를 기본 원칙으로 두는 것을 권장한다.

## 13. 타임리프 구현 원칙

### 일반 텍스트

```html
<h2 th:text="#{home.hero.title}"></h2>
```

### HTML 포함 텍스트

```html
<p th:utext="#{hero.message}"></p>
```

### 언어 전환 링크

```html
<a th:href="@{/ko/home}">KO</a>
<a th:href="@{/en/home}">EN</a>
```

또는 현재 경로 유지형으로 locale만 바꾸는 URL 재작성 로직을 둔다.

## 14. 컨트롤러 구조 예시

```java
@Controller
@RequestMapping("/{lang}")
public class HomeController {

    @GetMapping("/home")
    public String home(@PathVariable String lang, Model model, Locale locale) {
        return "home";
    }
}
```

여기서 `lang`은 `ko`, `en`만 허용하도록 인터셉터나 공통 검증 로직을 둔다.

## 15. 템플릿 구조 예시

```text
templates/
  layout/
    header.html
    footer.html
  home.html
  job/
    intro.html
    interview-list.html
    interview-detail.html
  culture/
    working.html
    benefits.html
  apply/
    process.html
    faq.html
```

## 16. 데이터 구조 전략

초기에는 정적 콘텐츠여도, 바로 하드코딩하지 말고 최소한 다음 형태로 추상화하는 것이 좋다.

* 메뉴
* 섹션 제목
* 본문 카피
* 카드 목록
* FAQ 목록
* 채용 절차 단계 목록

초기 1차는 properties + Java DTO 조합으로도 충분하다.
게시물성 콘텐츠가 많아지면 DB 또는 CMS 연동으로 확장한다.

## 17. SEO 요구사항

* 언어별 title/description 분리
* `hreflang="ko"` / `hreflang="en"` 제공
* 동일 콘텐츠의 언어 버전 canonical 전략 정의
* OG 태그 언어별 분리

## 18. 예외 및 실패 처리

* 존재하지 않는 lang 접근 시 `ko` 리다이렉트
* 번역 키 누락 시 fallback 메시지 또는 모니터링 로그 남김
* HTML 포함 번역 키에서 비허용 태그 발견 시 서버단 경고 로그

## 19. 완료 기준

* `/ko/home`, `/en/home` 모두 정상 렌더링
* 공통 메뉴 전 페이지 언어 전환 가능
* 채용 절차, 문화, FAQ 페이지 구현 완료
* 번역 문자열 내부 `<br>` 렌더링 정상 동작
* 문장 어순이 한국어/영어 각각 자연스럽게 노출
* XSS 위험 없는 출력 정책 적용
* JDK 11 환경에서 실행 가능 

[1]: https://talent.skhynix.com/hub/ko/home "SK hynix Talent Hub"
[2]: https://talent.skhynix.com/hub/ko/apply/process "SK hynix Talent Hub"
[3]: https://talent.skhynix.com/hub/ko/culture/working "SK hynix Talent Hub"
