package com.talenthub.web;

import com.talenthub.model.SiteViewModels.InterviewDetail;
import com.talenthub.model.SiteViewModels.JobRoleDetail;
import com.talenthub.model.SiteViewModels.SeoData;
import com.talenthub.model.SiteViewModels.SectionPage;
import com.talenthub.service.SiteContentService;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequestMapping("/{lang:ko|en}")
public class SiteController {

    private final SiteContentService contentService;

    public SiteController(SiteContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/home")
    public String home(@PathVariable String lang, HttpServletRequest request, Locale locale, Model model) {
        addCommon(model, request, lang, locale, contentService.seo("home", locale));
        model.addAttribute("stats", contentService.homeStats(locale));
        model.addAttribute("sectionCards", contentService.homeSectionCards(locale, lang));
        model.addAttribute("jobHighlights", contentService.jobRoles(locale, lang));
        model.addAttribute("interviewHighlights", limit(contentService.interviews(locale, lang), 2));
        model.addAttribute("cultureHighlights", contentService.workingValues(locale));
        model.addAttribute("processSteps", contentService.processSteps(locale));
        return "home";
    }

    @GetMapping("/company/{section:newsroom|story|campus}")
    public String companySection(@PathVariable String lang, @PathVariable String section, HttpServletRequest request,
            Locale locale, Model model) {
        SectionPage sectionPage = contentService.companySection(section, locale);
        addCommon(model, request, lang, locale, sectionPage.getSeo());
        model.addAttribute("sectionPage", sectionPage);
        return "company/list";
    }

    @GetMapping("/job/intro")
    public String jobIntro(@PathVariable String lang, HttpServletRequest request, Locale locale, Model model) {
        addCommon(model, request, lang, locale, contentService.seo("job.intro", locale));
        model.addAttribute("jobRoles", contentService.jobRoles(locale, lang));
        return "job/intro-list";
    }

    @GetMapping("/job/intro/{slug}")
    public String jobIntroDetail(@PathVariable String lang, @PathVariable String slug, HttpServletRequest request,
            Locale locale, Model model) {
        JobRoleDetail role = contentService.jobRole(slug, locale);
        addCommon(model, request, lang, locale, detailSeo(role.getTitle(), role.getSummary(), locale));
        model.addAttribute("role", role);
        model.addAttribute("backUrl", "/" + lang + "/job/intro");
        return "job/intro-detail";
    }

    @GetMapping("/job/interview")
    public String interviews(@PathVariable String lang, HttpServletRequest request, Locale locale, Model model) {
        addCommon(model, request, lang, locale, contentService.seo("job.interview", locale));
        model.addAttribute("interviews", contentService.interviews(locale, lang));
        return "job/interview-list";
    }

    @GetMapping("/job/interview/{slug}")
    public String interviewDetail(@PathVariable String lang, @PathVariable String slug, HttpServletRequest request,
            Locale locale, Model model) {
        InterviewDetail interview = contentService.interview(slug, locale);
        addCommon(model, request, lang, locale, detailSeo(interview.getTitle(), interview.getSummary(), locale));
        model.addAttribute("interview", interview);
        model.addAttribute("backUrl", "/" + lang + "/job/interview");
        return "job/interview-detail";
    }

    @GetMapping("/culture/working")
    public String cultureWorking(@PathVariable String lang, HttpServletRequest request, Locale locale, Model model) {
        addCommon(model, request, lang, locale, contentService.seo("culture.working", locale));
        model.addAttribute("values", contentService.workingValues(locale));
        return "culture/working";
    }

    @GetMapping("/culture/benefits")
    public String cultureBenefits(@PathVariable String lang, HttpServletRequest request, Locale locale, Model model) {
        addCommon(model, request, lang, locale, contentService.seo("culture.benefits", locale));
        model.addAttribute("benefitGroups", contentService.benefitGroups(locale));
        return "culture/benefits";
    }

    @GetMapping("/apply/process")
    public String process(@PathVariable String lang, HttpServletRequest request, Locale locale, Model model) {
        addCommon(model, request, lang, locale, contentService.seo("apply.process", locale));
        model.addAttribute("processSteps", contentService.processSteps(locale));
        return "apply/process";
    }

    @GetMapping("/apply/faq")
    public String faq(@PathVariable String lang, HttpServletRequest request, Locale locale, Model model) {
        addCommon(model, request, lang, locale, contentService.seo("apply.faq", locale));
        model.addAttribute("faqCategories", contentService.faqCategories(locale));
        return "apply/faq";
    }

    @GetMapping("/apply/{section:hy-way|openings}")
    public String applySection(@PathVariable String lang, @PathVariable String section, HttpServletRequest request,
            Locale locale, Model model) {
        SectionPage sectionPage = contentService.applySection(section, locale);
        addCommon(model, request, lang, locale, sectionPage.getSeo());
        model.addAttribute("sectionPage", sectionPage);
        return "apply/cards";
    }

    private void addCommon(Model model, HttpServletRequest request, String lang, Locale locale, SeoData seo) {
        String currentPath = currentPath(request);
        model.addAttribute("seo", seo);
        model.addAttribute("lang", lang);
        model.addAttribute("navGroups", contentService.navigation(locale, lang, currentPath));
        model.addAttribute("langSwitchUrls", languageSwitchUrls(currentPath));
        model.addAttribute("canonicalUrl", absoluteUrl(request, currentPath));
        model.addAttribute("alternateUrls", alternateUrls(request, currentPath));
        model.addAttribute("currentYear", Year.now().getValue());
    }

    private SeoData detailSeo(String title, String description, Locale locale) {
        return new SeoData(title + " | " + contentService.siteSuffix(locale), description);
    }

    private Map<String, String> languageSwitchUrls(String currentPath) {
        Map<String, String> switchUrls = new LinkedHashMap<>();
        switchUrls.put("ko", switchLanguage(currentPath, "ko"));
        switchUrls.put("en", switchLanguage(currentPath, "en"));
        return switchUrls;
    }

    private Map<String, String> alternateUrls(HttpServletRequest request, String currentPath) {
        Map<String, String> urls = new LinkedHashMap<>();
        urls.put("ko", absoluteUrl(request, switchLanguage(currentPath, "ko")));
        urls.put("en", absoluteUrl(request, switchLanguage(currentPath, "en")));
        urls.put("x-default", absoluteUrl(request, "/ko/home"));
        return urls;
    }

    private String switchLanguage(String path, String targetLang) {
        String[] segments = StringUtils.tokenizeToStringArray(path, "/");
        if (segments.length == 0) {
            return "/" + targetLang + "/home";
        }
        segments[0] = targetLang;
        return "/" + String.join("/", segments);
    }

    private String absoluteUrl(HttpServletRequest request, String path) {
        String contextPath = request.getContextPath();
        String absolutePath = (StringUtils.hasText(contextPath) ? contextPath : "") + path;
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(absolutePath)
                .replaceQuery(null)
                .build()
                .toUriString();
    }

    private String currentPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        if (StringUtils.hasText(contextPath) && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }
        return StringUtils.hasText(uri) ? uri : "/";
    }

    private <T> List<T> limit(List<T> values, int max) {
        return values.subList(0, Math.min(values.size(), max));
    }
}
