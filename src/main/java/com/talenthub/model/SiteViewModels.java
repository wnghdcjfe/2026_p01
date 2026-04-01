package com.talenthub.model;

import java.util.List;

public final class SiteViewModels {

    private SiteViewModels() {
    }

    public static final class SeoData {
        private final String title;
        private final String description;

        public SeoData(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

    public static final class NavItem {
        private final String label;
        private final String url;
        private final boolean active;

        public NavItem(String label, String url, boolean active) {
            this.label = label;
            this.url = url;
            this.active = active;
        }

        public String getLabel() {
            return label;
        }

        public String getUrl() {
            return url;
        }

        public boolean isActive() {
            return active;
        }
    }

    public static final class NavGroup {
        private final String label;
        private final List<NavItem> items;
        private final boolean active;

        public NavGroup(String label, List<NavItem> items, boolean active) {
            this.label = label;
            this.items = items;
            this.active = active;
        }

        public String getLabel() {
            return label;
        }

        public List<NavItem> getItems() {
            return items;
        }

        public boolean isActive() {
            return active;
        }
    }

    public static final class HighlightStat {
        private final String label;
        private final String value;
        private final String description;

        public HighlightStat(String label, String value, String description) {
            this.label = label;
            this.value = value;
            this.description = description;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }

    public static final class ContentCard {
        private final String eyebrow;
        private final String title;
        private final String description;
        private final String url;
        private final String actionLabel;

        public ContentCard(String eyebrow, String title, String description, String url, String actionLabel) {
            this.eyebrow = eyebrow;
            this.title = title;
            this.description = description;
            this.url = url;
            this.actionLabel = actionLabel;
        }

        public String getEyebrow() {
            return eyebrow;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getUrl() {
            return url;
        }

        public String getActionLabel() {
            return actionLabel;
        }
    }

    public static final class SectionPage {
        private final String eyebrow;
        private final String title;
        private final String lead;
        private final List<ContentCard> cards;
        private final SeoData seo;

        public SectionPage(String eyebrow, String title, String lead, List<ContentCard> cards, SeoData seo) {
            this.eyebrow = eyebrow;
            this.title = title;
            this.lead = lead;
            this.cards = cards;
            this.seo = seo;
        }

        public String getEyebrow() {
            return eyebrow;
        }

        public String getTitle() {
            return title;
        }

        public String getLead() {
            return lead;
        }

        public List<ContentCard> getCards() {
            return cards;
        }

        public SeoData getSeo() {
            return seo;
        }
    }

    public static final class JobRoleSummary {
        private final String slug;
        private final String family;
        private final String title;
        private final String summary;
        private final String focus;
        private final List<String> skills;
        private final String url;

        public JobRoleSummary(String slug, String family, String title, String summary, String focus, List<String> skills,
                String url) {
            this.slug = slug;
            this.family = family;
            this.title = title;
            this.summary = summary;
            this.focus = focus;
            this.skills = skills;
            this.url = url;
        }

        public String getSlug() {
            return slug;
        }

        public String getFamily() {
            return family;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public String getFocus() {
            return focus;
        }

        public List<String> getSkills() {
            return skills;
        }

        public String getUrl() {
            return url;
        }
    }

    public static final class JobRoleDetail {
        private final String slug;
        private final String family;
        private final String title;
        private final String heroHtml;
        private final String summary;
        private final String overview;
        private final List<String> skills;
        private final List<String> responsibilities;
        private final List<String> collaborations;
        private final List<String> growthPoints;

        public JobRoleDetail(String slug, String family, String title, String heroHtml, String summary, String overview,
                List<String> skills, List<String> responsibilities, List<String> collaborations,
                List<String> growthPoints) {
            this.slug = slug;
            this.family = family;
            this.title = title;
            this.heroHtml = heroHtml;
            this.summary = summary;
            this.overview = overview;
            this.skills = skills;
            this.responsibilities = responsibilities;
            this.collaborations = collaborations;
            this.growthPoints = growthPoints;
        }

        public String getSlug() {
            return slug;
        }

        public String getFamily() {
            return family;
        }

        public String getTitle() {
            return title;
        }

        public String getHeroHtml() {
            return heroHtml;
        }

        public String getSummary() {
            return summary;
        }

        public String getOverview() {
            return overview;
        }

        public List<String> getSkills() {
            return skills;
        }

        public List<String> getResponsibilities() {
            return responsibilities;
        }

        public List<String> getCollaborations() {
            return collaborations;
        }

        public List<String> getGrowthPoints() {
            return growthPoints;
        }
    }

    public static final class InterviewSummary {
        private final String slug;
        private final String title;
        private final String summary;
        private final String person;
        private final String role;
        private final String quote;
        private final String url;

        public InterviewSummary(String slug, String title, String summary, String person, String role, String quote,
                String url) {
            this.slug = slug;
            this.title = title;
            this.summary = summary;
            this.person = person;
            this.role = role;
            this.quote = quote;
            this.url = url;
        }

        public String getSlug() {
            return slug;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public String getPerson() {
            return person;
        }

        public String getRole() {
            return role;
        }

        public String getQuote() {
            return quote;
        }

        public String getUrl() {
            return url;
        }
    }

    public static final class InterviewDetail {
        private final String slug;
        private final String title;
        private final String summary;
        private final String person;
        private final String role;
        private final String quote;
        private final List<String> storyParagraphs;
        private final List<String> takeaways;

        public InterviewDetail(String slug, String title, String summary, String person, String role, String quote,
                List<String> storyParagraphs, List<String> takeaways) {
            this.slug = slug;
            this.title = title;
            this.summary = summary;
            this.person = person;
            this.role = role;
            this.quote = quote;
            this.storyParagraphs = storyParagraphs;
            this.takeaways = takeaways;
        }

        public String getSlug() {
            return slug;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public String getPerson() {
            return person;
        }

        public String getRole() {
            return role;
        }

        public String getQuote() {
            return quote;
        }

        public List<String> getStoryParagraphs() {
            return storyParagraphs;
        }

        public List<String> getTakeaways() {
            return takeaways;
        }
    }

    public static final class ValueCard {
        private final String title;
        private final String summary;
        private final List<String> points;

        public ValueCard(String title, String summary, List<String> points) {
            this.title = title;
            this.summary = summary;
            this.points = points;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public List<String> getPoints() {
            return points;
        }
    }

    public static final class BenefitGroup {
        private final String title;
        private final String summary;
        private final List<String> items;

        public BenefitGroup(String title, String summary, List<String> items) {
            this.title = title;
            this.summary = summary;
            this.items = items;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public List<String> getItems() {
            return items;
        }
    }

    public static final class ProcessStep {
        private final String stage;
        private final String title;
        private final String description;
        private final String note;

        public ProcessStep(String stage, String title, String description, String note) {
            this.stage = stage;
            this.title = title;
            this.description = description;
            this.note = note;
        }

        public String getStage() {
            return stage;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getNote() {
            return note;
        }
    }

    public static final class FaqItem {
        private final String question;
        private final String answer;

        public FaqItem(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }
    }

    public static final class FaqCategory {
        private final String title;
        private final List<FaqItem> items;

        public FaqCategory(String title, List<FaqItem> items) {
            this.title = title;
            this.items = items;
        }

        public String getTitle() {
            return title;
        }

        public List<FaqItem> getItems() {
            return items;
        }
    }
}
