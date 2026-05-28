package com.talenthub.config;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.util.StringUtils;

public final class LocalePolicy {

    public static final String KOREAN_LANGUAGE = "ko";
    public static final String ENGLISH_LANGUAGE = "en";
    public static final String SUPPORTED_LANGUAGE_PATH_PATTERN = "/{lang:" + KOREAN_LANGUAGE + "|" + ENGLISH_LANGUAGE + "}";
    public static final Locale DEFAULT_LOCALE = Locale.KOREAN;
    public static final String DEFAULT_HOME_PATH = "/ko/home";

    private static final Map<String, Locale> LOCALES_BY_LANGUAGE = Map.of(
            KOREAN_LANGUAGE, Locale.KOREAN,
            ENGLISH_LANGUAGE, Locale.ENGLISH);
    private static final Set<String> INFRASTRUCTURE_PATH_PREFIXES = Set.of(
            "/api/",
            "/css/",
            "/js/",
            "/images/",
            "/webjars/",
            "/actuator/",
            "/.well-known/");
    private static final Set<String> INFRASTRUCTURE_PATHS = Set.of(
            "/api",
            "/actuator",
            "/.well-known",
            "/error",
            "/favicon.ico",
            "/robots.txt",
            "/sitemap.xml");

    private LocalePolicy() {
    }

    public static boolean isSupportedLanguage(String language) {
        return LOCALES_BY_LANGUAGE.containsKey(normalizeLanguage(language));
    }

    public static Locale localeForLanguage(String language) {
        return LOCALES_BY_LANGUAGE.getOrDefault(normalizeLanguage(language), DEFAULT_LOCALE);
    }

    public static Optional<String> firstPathSegment(String path) {
        String[] segments = StringUtils.tokenizeToStringArray(path, "/");
        if (segments.length == 0) {
            return Optional.empty();
        }
        return Optional.of(segments[0]);
    }

    public static boolean isInfrastructurePath(String path) {
        if (!StringUtils.hasText(path)) {
            return false;
        }
        if (INFRASTRUCTURE_PATHS.contains(path)) {
            return true;
        }
        return INFRASTRUCTURE_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }

    public static String normalizeLanguage(String language) {
        return StringUtils.hasText(language) ? language.toLowerCase(Locale.ROOT) : "";
    }
}
