package com.talenthub.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RouterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rootRedirectsToKoreanHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ko/home"));
    }

    @Test
    void invalidLanguageRedirectsToDefaultLocale() throws Exception {
        mockMvc.perform(get("/fr/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ko/home"));
    }

    @Test
    void uppercaseLanguageRedirectsToCanonicalLowercasePath() throws Exception {
        mockMvc.perform(get("/EN/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/en/home"));
    }

    @Test
    void apiRoutesAreNotCapturedByLocaleRedirects() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isNotFound());
    }

    @Test
    void pageShapedInfrastructureRoutesAreNotCapturedByRouterController() throws Exception {
        mockMvc.perform(get("/api/home"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/actuator/home"))
                .andExpect(status().isNotFound());
    }

    @Test
    void englishHomeRendersTrustedHtmlAndSeoAlternates() throws Exception {
        mockMvc.perform(get("/en/home"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<html lang=\"en\"")))
                .andExpect(content().string(containsString("Skip to content")))
                .andExpect(content().string(containsString("<strong>your next career chapter</strong>")))
                .andExpect(content().string(containsString("hreflang=\"ko\"")))
                .andExpect(content().string(containsString("/ko/home")));
    }

    @Test
    void koreanHomeRendersKoreanBundleThroughThymeleaf() throws Exception {
        mockMvc.perform(get("/ko/home"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<html lang=\"ko\"")))
                .andExpect(content().string(containsString("본문으로 건너뛰기")))
                .andExpect(content().string(containsString("<strong>당신의 다음 커리어</strong>")));
    }

    @Test
    void detailPageKeepsLanguageSwitchPath() throws Exception {
        mockMvc.perform(get("/en/job/intro/ai-platform"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("AI service experience")))
                .andExpect(content().string(containsString("/ko/job/intro/ai-platform")));
    }

    @Test
    void faqPageRendersSearchUi() throws Exception {
        mockMvc.perform(get("/ko/apply/faq"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-faq-search")))
                .andExpect(content().string(containsString("자주 묻는 질문")));
    }
}
