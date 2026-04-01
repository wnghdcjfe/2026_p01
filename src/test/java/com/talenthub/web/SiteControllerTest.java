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
class SiteControllerTest {

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
    void englishHomeRendersTrustedHtmlAndSeoAlternates() throws Exception {
        mockMvc.perform(get("/en/home"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<strong>your next career chapter</strong>")))
                .andExpect(content().string(containsString("hreflang=\"ko\"")))
                .andExpect(content().string(containsString("/ko/home")));
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
