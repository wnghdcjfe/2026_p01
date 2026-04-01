package com.talenthub.service;

import com.talenthub.model.SiteViewModels.BenefitGroup;
import com.talenthub.model.SiteViewModels.ContentCard;
import com.talenthub.model.SiteViewModels.FaqCategory;
import com.talenthub.model.SiteViewModels.FaqItem;
import com.talenthub.model.SiteViewModels.HighlightStat;
import com.talenthub.model.SiteViewModels.InterviewDetail;
import com.talenthub.model.SiteViewModels.InterviewSummary;
import com.talenthub.model.SiteViewModels.JobRoleDetail;
import com.talenthub.model.SiteViewModels.JobRoleSummary;
import com.talenthub.model.SiteViewModels.NavGroup;
import com.talenthub.model.SiteViewModels.NavItem;
import com.talenthub.model.SiteViewModels.ProcessStep;
import com.talenthub.model.SiteViewModels.SectionPage;
import com.talenthub.model.SiteViewModels.SeoData;
import com.talenthub.model.SiteViewModels.ValueCard;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SiteContentService {

    private static final List<String> JOB_ROLE_SLUGS = List.of("memory-architecture", "process-integration", "ai-platform");
    private static final List<String> INTERVIEW_SLUGS = List.of("device-lead", "data-strategist", "culture-builder");
    private static final List<String> CULTURE_VALUE_IDS = List.of("bar-raising", "data-driven", "one-team");
    private static final List<String> BENEFIT_GROUP_IDS = List.of("growth", "wellbeing", "flexibility", "support");
    private static final List<String> FAQ_CATEGORY_IDS = List.of("application", "interview", "global");
    private static final Map<String, List<String>> COMPANY_CARD_IDS = Map.of(
            "newsroom", List.of("memory-roadmap", "sustainable-operations", "global-campus"),
            "story", List.of("mentorship-loop", "career-switching", "hyway-journey"),
            "campus", List.of("seoul-hub", "icheon-campus", "community-labs"));
    private static final Map<String, List<String>> APPLY_CARD_IDS = Map.of(
            "hy-way", List.of("orientation", "monthly-challenge", "mentor-office-hours"),
            "openings", List.of("ai-service-planner", "process-innovation-engineer", "global-employer-brand-manager"));
    private static final Map<String, List<String>> FAQ_ITEM_IDS = Map.of(
            "application", List.of("resume", "portfolio", "timeline"),
            "interview", List.of("format", "language", "feedback"),
            "global", List.of("english", "relocation", "visa"));

    private final TrustedMessageService messages;

    public SiteContentService(TrustedMessageService messages) {
        this.messages = messages;
    }

    public SeoData seo(String pageKey, Locale locale) {
        return new SeoData(text("seo." + pageKey + ".title", locale), text("seo." + pageKey + ".description", locale));
    }

    public String siteSuffix(Locale locale) {
        return text("seo.site.suffix", locale);
    }

    public List<NavGroup> navigation(Locale locale, String lang, String currentPath) {
        return List.of(
                navGroup(text("nav.company", locale), List.of(
                        navItem(locale, lang, currentPath, "company/newsroom", "nav.company.newsroom"),
                        navItem(locale, lang, currentPath, "company/story", "nav.company.story"),
                        navItem(locale, lang, currentPath, "company/campus", "nav.company.campus"))),
                navGroup(text("nav.job", locale), List.of(
                        navItem(locale, lang, currentPath, "job/intro", "nav.job.intro"),
                        navItem(locale, lang, currentPath, "job/interview", "nav.job.interview"))),
                navGroup(text("nav.culture", locale), List.of(
                        navItem(locale, lang, currentPath, "culture/working", "nav.culture.working"),
                        navItem(locale, lang, currentPath, "culture/benefits", "nav.culture.benefits"))),
                navGroup(text("nav.apply", locale), List.of(
                        navItem(locale, lang, currentPath, "apply/hy-way", "nav.apply.hyway"),
                        navItem(locale, lang, currentPath, "apply/openings", "nav.apply.openings"),
                        navItem(locale, lang, currentPath, "apply/process", "nav.apply.process"),
                        navItem(locale, lang, currentPath, "apply/faq", "nav.apply.faq"))));
    }

    public List<HighlightStat> homeStats(Locale locale) {
        return List.of(
                stat(locale, "home.stats.culture"),
                stat(locale, "home.stats.roles"),
                stat(locale, "home.stats.global"));
    }

    public List<ContentCard> homeSectionCards(Locale locale, String lang) {
        return List.of(
                linkCard(locale, "home.sections.company", path(lang, "company/newsroom"), "common.exploreSection"),
                linkCard(locale, "home.sections.job", path(lang, "job/intro"), "common.exploreSection"),
                linkCard(locale, "home.sections.culture", path(lang, "culture/working"), "common.exploreSection"),
                linkCard(locale, "home.sections.apply", path(lang, "apply/process"), "common.exploreSection"));
    }

    public SectionPage companySection(String section, Locale locale) {
        return sectionPage("company." + section, COMPANY_CARD_IDS.get(section), locale);
    }

    public SectionPage applySection(String section, Locale locale) {
        return sectionPage("apply.sections." + section, APPLY_CARD_IDS.get(section), locale);
    }

    public List<JobRoleSummary> jobRoles(Locale locale, String lang) {
        return JOB_ROLE_SLUGS.stream().map(slug -> jobRoleSummary(slug, locale, lang)).collect(Collectors.toList());
    }

    public JobRoleDetail jobRole(String slug, Locale locale) {
        if (!JOB_ROLE_SLUGS.contains(slug)) {
            throw notFound();
        }
        String baseKey = "job.roles." + slug;
        return new JobRoleDetail(
                slug,
                text(baseKey + ".family", locale),
                text(baseKey + ".title", locale),
                messages.html(baseKey + ".heroHtml", locale),
                text(baseKey + ".summary", locale),
                text(baseKey + ".overview", locale),
                list(baseKey + ".skills", locale),
                list(baseKey + ".responsibilities", locale),
                list(baseKey + ".collaboration", locale),
                list(baseKey + ".growth", locale));
    }

    public List<InterviewSummary> interviews(Locale locale, String lang) {
        return INTERVIEW_SLUGS.stream().map(slug -> interviewSummary(slug, locale, lang)).collect(Collectors.toList());
    }

    public InterviewDetail interview(String slug, Locale locale) {
        if (!INTERVIEW_SLUGS.contains(slug)) {
            throw notFound();
        }
        String baseKey = "job.interviews." + slug;
        return new InterviewDetail(
                slug,
                text(baseKey + ".title", locale),
                text(baseKey + ".summary", locale),
                text(baseKey + ".person", locale),
                text(baseKey + ".role", locale),
                text(baseKey + ".quote", locale),
                list(baseKey + ".story", locale),
                list(baseKey + ".takeaways", locale));
    }

    public List<ValueCard> workingValues(Locale locale) {
        return CULTURE_VALUE_IDS.stream()
                .map(id -> new ValueCard(
                        text("culture.values." + id + ".title", locale),
                        text("culture.values." + id + ".summary", locale),
                        list("culture.values." + id + ".points", locale)))
                .collect(Collectors.toList());
    }

    public List<BenefitGroup> benefitGroups(Locale locale) {
        return BENEFIT_GROUP_IDS.stream()
                .map(id -> new BenefitGroup(
                        text("culture.benefits.groups." + id + ".title", locale),
                        text("culture.benefits.groups." + id + ".summary", locale),
                        list("culture.benefits.groups." + id + ".items", locale)))
                .collect(Collectors.toList());
    }

    public List<ProcessStep> processSteps(Locale locale) {
        return List.of(step(locale, 1), step(locale, 2), step(locale, 3), step(locale, 4));
    }

    public List<FaqCategory> faqCategories(Locale locale) {
        return FAQ_CATEGORY_IDS.stream()
                .map(categoryId -> new FaqCategory(
                        text("apply.faq.categories." + categoryId + ".title", locale),
                        FAQ_ITEM_IDS.get(categoryId).stream()
                                .map(itemId -> new FaqItem(
                                        text("apply.faq.categories." + categoryId + "." + itemId + ".question", locale),
                                        text("apply.faq.categories." + categoryId + "." + itemId + ".answer", locale)))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    private JobRoleSummary jobRoleSummary(String slug, Locale locale, String lang) {
        String baseKey = "job.roles." + slug;
        return new JobRoleSummary(
                slug,
                text(baseKey + ".family", locale),
                text(baseKey + ".title", locale),
                text(baseKey + ".summary", locale),
                text(baseKey + ".focus", locale),
                list(baseKey + ".skills", locale),
                path(lang, "job/intro/" + slug));
    }

    private InterviewSummary interviewSummary(String slug, Locale locale, String lang) {
        String baseKey = "job.interviews." + slug;
        return new InterviewSummary(
                slug,
                text(baseKey + ".title", locale),
                text(baseKey + ".summary", locale),
                text(baseKey + ".person", locale),
                text(baseKey + ".role", locale),
                text(baseKey + ".quote", locale),
                path(lang, "job/interview/" + slug));
    }

    private SectionPage sectionPage(String baseKey, List<String> cardIds, Locale locale) {
        if (cardIds == null) {
            throw notFound();
        }
        List<ContentCard> cards = cardIds.stream()
                .map(cardId -> {
                    String cardKey = baseKey + ".cards." + cardId;
                    return new ContentCard(
                            text(cardKey + ".eyebrow", locale),
                            text(cardKey + ".title", locale),
                            text(cardKey + ".description", locale),
                            null,
                            null);
                })
                .collect(Collectors.toList());
        return new SectionPage(
                text(baseKey + ".eyebrow", locale),
                text(baseKey + ".title", locale),
                text(baseKey + ".lead", locale),
                cards,
                seo(baseKey, locale));
    }

    private HighlightStat stat(Locale locale, String key) {
        return new HighlightStat(
                text(key + ".label", locale),
                text(key + ".value", locale),
                text(key + ".description", locale));
    }

    private ProcessStep step(Locale locale, int index) {
        String baseKey = "apply.process.steps.step" + index;
        return new ProcessStep(
                text(baseKey + ".stage", locale),
                text(baseKey + ".title", locale),
                text(baseKey + ".description", locale),
                text(baseKey + ".note", locale));
    }

    private ContentCard linkCard(Locale locale, String key, String url, String actionLabelKey) {
        return new ContentCard(
                text(key + ".eyebrow", locale),
                text(key + ".title", locale),
                text(key + ".description", locale),
                url,
                text(actionLabelKey, locale));
    }

    private NavGroup navGroup(String label, List<NavItem> items) {
        boolean active = items.stream().anyMatch(NavItem::isActive);
        return new NavGroup(label, items, active);
    }

    private NavItem navItem(Locale locale, String lang, String currentPath, String relativePath, String labelKey) {
        String url = path(lang, relativePath);
        boolean active = currentPath.equals(url) || currentPath.startsWith(url + "/");
        return new NavItem(text(labelKey, locale), url, active);
    }

    private String path(String lang, String relativePath) {
        return "/" + lang + "/" + relativePath;
    }

    private String text(String key, Locale locale) {
        return messages.text(key, locale);
    }

    private List<String> list(String key, Locale locale) {
        return messages.list(key, locale);
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
