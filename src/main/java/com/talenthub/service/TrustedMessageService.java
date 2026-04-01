package com.talenthub.service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class TrustedMessageService {

    private static final Logger log = LoggerFactory.getLogger(TrustedMessageService.class);
    private static final Locale FALLBACK_LOCALE = Locale.KOREAN;
    private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Set<String> ALLOWED_TAGS = Set.of("<br>", "<br/>", "<strong>", "</strong>", "<em>", "</em>");

    private final MessageSource messageSource;

    public TrustedMessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String text(String key, Locale locale, Object... args) {
        String resolved = resolveMessage(key, locale, args);
        if (resolved == null && !FALLBACK_LOCALE.equals(locale)) {
            resolved = resolveMessage(key, FALLBACK_LOCALE, args);
        }
        if (resolved == null) {
            log.warn("Missing message key '{}' for locale '{}'", key, locale);
            return key;
        }
        return resolved;
    }

    public List<String> list(String key, Locale locale) {
        return Arrays.stream(text(key, locale).split("\\s*\\|\\s*"))
                .filter(value -> !value.isBlank())
                .collect(Collectors.toList());
    }

    public String html(String key, Locale locale) {
        return sanitizeTrustedHtml(text(key, locale), key, locale);
    }

    public String sanitize(String raw) {
        return sanitizeTrustedHtml(raw, "manual", Locale.ROOT);
    }

    private String resolveMessage(String key, Locale locale, Object[] args) {
        return messageSource.getMessage(key, args, null, locale);
    }

    private String sanitizeTrustedHtml(String raw, String key, Locale locale) {
        Matcher matcher = TAG_PATTERN.matcher(raw);
        while (matcher.find()) {
            String tag = matcher.group();
            if (!isAllowed(tag)) {
                log.warn("Disallowed trusted HTML tag '{}' found in message key '{}' for locale '{}'", tag, key, locale);
            }
        }

        String escaped = HtmlUtils.htmlEscape(raw);
        return escaped.replace("&lt;br&gt;", "<br>")
                .replace("&lt;br/&gt;", "<br/>")
                .replace("&lt;br /&gt;", "<br/>")
                .replace("&lt;strong&gt;", "<strong>")
                .replace("&lt;/strong&gt;", "</strong>")
                .replace("&lt;em&gt;", "<em>")
                .replace("&lt;/em&gt;", "</em>");
    }

    private boolean isAllowed(String tag) {
        String normalized = tag.toLowerCase(Locale.ROOT).replace(" ", "");
        return ALLOWED_TAGS.contains(normalized);
    }
}
