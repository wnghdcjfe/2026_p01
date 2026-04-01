package com.talenthub.config;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

public class PathLocaleResolver implements LocaleResolver {

    private static final Locale DEFAULT_LOCALE = Locale.KOREAN;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String path = pathWithinApplication(request);
        String[] segments = StringUtils.tokenizeToStringArray(path, "/");
        if (segments.length > 0 && "en".equalsIgnoreCase(segments[0])) {
            return Locale.ENGLISH;
        }
        return DEFAULT_LOCALE;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        // Path-based locale routing is immutable per request.
    }

    private String pathWithinApplication(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        if (StringUtils.hasText(contextPath) && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }
}
