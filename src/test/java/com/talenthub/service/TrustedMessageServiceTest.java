package com.talenthub.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;

class TrustedMessageServiceTest {

    @Test
    void keepsAllowedTagsAndEscapesDisallowedOnes() {
        StaticMessageSource source = new StaticMessageSource();
        source.addMessage("safe", Locale.ENGLISH, "Hello <strong>world</strong><br/>");
        source.addMessage("unsafe", Locale.ENGLISH, "Hello <script>alert(1)</script>");

        TrustedMessageService service = new TrustedMessageService(source);

        assertThat(service.html("safe", Locale.ENGLISH)).contains("<strong>world</strong>").contains("<br/>");
        assertThat(service.html("unsafe", Locale.ENGLISH)).contains("&lt;script&gt;").doesNotContain("<script>");
    }

    @Test
    void fallsBackToKoreanWhenLocaleSpecificMessageIsMissing() {
        StaticMessageSource source = new StaticMessageSource();
        source.addMessage("greeting", Locale.KOREAN, "안녕하세요");

        TrustedMessageService service = new TrustedMessageService(source);

        assertThat(service.text("greeting", Locale.ENGLISH)).isEqualTo("안녕하세요");
    }
}
