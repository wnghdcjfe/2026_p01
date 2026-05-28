# error_01: `lang=en`인데 `messages_en`이 적용되지 않는 경우

## 질문

Thymeleaf로 렌더링된 HTML의 `lang` 값은 `en`이고, `RouterController`의 `@PathVariable String lang`도 `en`인데, 실제 메시지는 `messages_en.properties`가 아니라 다른 locale 메시지로 렌더링되는 상황을 만들 수 있는가?

## 현재 구현 기준 결론

현재 코드와 정상 브라우저 요청 기준으로는 거의 불가능하다.

`/en/home` 요청에서는 다음 흐름이 모두 `en`으로 정렬된다.

1. `RouterController`는 `/{lang:ko|en}` 패턴만 받는다.
   - `/en/home`이면 `@PathVariable lang = "en"`
2. `PathLocaleResolver`는 요청 URI의 첫 path segment를 읽는다.
   - `/en/home`이면 `Locale.ENGLISH` 반환
3. Thymeleaf의 `#{...}` 메시지 표현식은 `${lang}` 모델 값이 아니라 Spring `LocaleResolver`가 반환한 locale을 사용한다.
4. 따라서 `messages_en.properties`가 적용된다.

실행 서버 확인 결과도 다음과 같았다.

```text
/en/home
- <html lang="en"> 있음
- Skip to content 있음
- your next career chapter 있음
- 본문으로 건너뛰기 없음
- 당신의 다음 커리어 없음
```

## 관련 코드 위치

- `src/main/java/com/talenthub/web/RouterController.java`
  - `@RequestMapping(LocalePolicy.SUPPORTED_LANGUAGE_PATH_PATTERN)`
  - 지원 언어 path만 컨트롤러에 매핑된다.
- `src/main/java/com/talenthub/config/LocalePolicy.java`
  - `SUPPORTED_LANGUAGE_PATH_PATTERN = "/{lang:ko|en}"`
  - `ko`, `en`, default locale, infrastructure path 정책을 중앙화한다.
- `src/main/java/com/talenthub/config/PathLocaleResolver.java`
  - request URI 첫 segment를 기준으로 locale을 결정한다.
- `src/main/java/com/talenthub/config/WebConfig.java`
  - `MessageSource`를 `classpath:messages`로 설정한다.
  - `messages_en.properties`, `messages_ko.properties`를 사용한다.
- `src/main/resources/templates/layout/base.html`
  - `<html th:lang="${lang}">`
  - HTML `lang`은 모델의 `lang` 값을 사용한다.

## 실제로 불일치를 만들 수 있는 경우

정상 요청이 아니라 설정이나 테스트 환경을 일부러 깨면 다음 상황은 만들 수 있다.

### 1. `LocaleResolver`가 다른 Bean으로 override되는 경우

예를 들어 `PathLocaleResolver` 대신 `AcceptHeaderLocaleResolver`, `SessionLocaleResolver` 등이 등록되면 URL은 `/en/home`이어도 실제 Spring locale은 `ko`가 될 수 있다.

결과:

```text
RouterController lang = en
<html lang="en">
Thymeleaf #{...} = messages_ko 적용 가능
```

### 2. `LocaleContextHolder`를 필터/인터셉터에서 강제로 바꾸는 경우

어떤 필터나 인터셉터가 request 처리 중에 `LocaleContextHolder`를 `ko`로 강제 설정하면, route/model 값은 `en`이어도 Thymeleaf 메시지 해석 locale이 달라질 수 있다.

### 3. `messages_en.properties`에 key가 누락되는 경우

직접 `#{some.key}`를 사용하는 Thymeleaf 메시지는 key 누락 시 fallback/오류 방식에 따라 기대와 다르게 보일 수 있다.

현재는 `MessageBundleParityTest`가 `messages_en.properties`와 `messages_ko.properties`의 key parity를 검증하므로 이 위험을 줄인다.

### 4. `TrustedMessageService` fallback이 동작하는 경우

`TrustedMessageService.text(...)`는 요청 locale에서 메시지를 못 찾으면 한국어 fallback을 시도한다.

따라서 서비스 경유 컨텐츠에서 영어 key가 빠져 있으면 다음이 가능하다.

```text
lang = en
locale = Locale.ENGLISH
messages_en key 없음
TrustedMessageService가 messages_ko로 fallback
```

다만 현재는 bundle parity 테스트가 영어 key 누락을 막는다.

### 5. MockMvc나 직접 컨트롤러 호출에서 request URI와 path variable을 다르게 조작하는 경우

실제 브라우저 요청에서는 URL path와 `@PathVariable`이 같은 라우팅에서 나오지만, 테스트에서 컨트롤러를 직접 호출하거나 forward/mock을 비정상 구성하면 다음처럼 만들 수 있다.

```text
path variable lang = en
request URI = /ko/home
PathLocaleResolver = Locale.KOREAN
```

이 경우 HTML `lang`은 `en`으로 나가면서 메시지는 한국어가 될 수 있다.

## 방지책

현재 코드에는 다음 방지책이 들어가 있다.

1. 컨트롤러 매핑 제한
   - `RouterController`는 `/{lang:ko|en}`만 받는다.
   - `/api/home` 같은 비페이지 path가 컨트롤러에 잡히지 않는다.
2. locale 정책 중앙화
   - `LocalePolicy`에서 지원 언어, default path, infrastructure path를 관리한다.
3. path 기반 locale resolver
   - `PathLocaleResolver`가 URL 첫 segment와 Spring locale을 맞춘다.
4. message bundle parity 테스트
   - `messages_en.properties`, `messages_ko.properties` key 불일치를 테스트로 막는다.
5. 회귀 테스트
   - `/en/home` 영어 렌더링 확인
   - `/ko/home` 한국어 렌더링 확인
   - `/api/home`, `/actuator/home`이 page controller에 잡히지 않는지 확인

## 점검 명령

서버 실행 중 다음으로 확인할 수 있다.

```sh
curl -s http://localhost:8080/en/home > /tmp/en-home.html

grep -q '<html lang="en"' /tmp/en-home.html && echo 'html lang en OK'
grep -q 'Skip to content' /tmp/en-home.html && echo 'messages_en OK'
! grep -q '본문으로 건너뛰기' /tmp/en-home.html && echo 'korean message not rendered OK'
```

테스트는 다음으로 확인한다.

```sh
./mvnw clean test
```

## 요약

현재 정상 경로에서는 `RouterController lang=en`, `<html lang="en">`, `messages_en.properties` 적용이 함께 움직인다.

불일치를 만들려면 다음 중 하나가 필요하다.

- `LocaleResolver` override
- `LocaleContextHolder` 강제 변경
- 영어 message key 누락
- `TrustedMessageService` fallback 유발
- 비정상 MockMvc/direct invocation 구성

따라서 이 문제는 현재 코드의 일반적인 런타임 버그라기보다, locale 결정 계층과 HTML `lang` 모델 값이 분리될 때 발생하는 설정/테스트/확장 위험으로 보는 것이 맞다.
