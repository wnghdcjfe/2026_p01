package com.talenthub.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class LocaleRoutingFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = pathWithinApplication(request);
        return LocalePolicy.isInfrastructurePath(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = pathWithinApplication(request);
        if (!StringUtils.hasText(path) || "/".equals(path)) {
            redirect(request, response, LocalePolicy.DEFAULT_HOME_PATH);
            return;
        }

        String lang = LocalePolicy.firstPathSegment(path).orElse("");
        if (lang.isEmpty()) {
            redirect(request, response, LocalePolicy.DEFAULT_HOME_PATH);
            return;
        }

        if (!LocalePolicy.isSupportedLanguage(lang)) {
            redirect(request, response, LocalePolicy.DEFAULT_HOME_PATH);
            return;
        }

        String normalizedLang = LocalePolicy.normalizeLanguage(lang);
        if (!lang.equals(normalizedLang)) {
            redirect(request, response, replaceLanguage(path, normalizedLang));
            return;
        }

        if (isLanguageRoot(path, lang)) {
            redirect(request, response, "/" + normalizedLang + "/home");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String target) throws IOException {
        response.sendRedirect(request.getContextPath() + target);
    }

    private String pathWithinApplication(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        if (StringUtils.hasText(contextPath) && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

    private boolean isLanguageRoot(String path, String lang) {
        return path.equals("/" + lang) || path.equals("/" + lang + "/");
    }

    private String replaceLanguage(String path, String normalizedLang) {
        String withoutLeadingSlash = path.startsWith("/") ? path.substring(1) : path;
        int slashIndex = withoutLeadingSlash.indexOf('/');
        if (slashIndex < 0) {
            return "/" + normalizedLang;
        }
        return "/" + normalizedLang + withoutLeadingSlash.substring(slashIndex);
    }
}
