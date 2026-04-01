package com.talenthub.config;

import java.io.IOException;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class LocaleRoutingFilter extends OncePerRequestFilter {

    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("ko", "en");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = pathWithinApplication(request);
        return path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/webjars/")
                || path.startsWith("/error")
                || path.startsWith("/actuator")
                || path.startsWith("/.well-known")
                || "/favicon.ico".equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = pathWithinApplication(request);
        if (!StringUtils.hasText(path) || "/".equals(path)) {
            redirect(request, response, "/ko/home");
            return;
        }

        String[] segments = StringUtils.tokenizeToStringArray(path, "/");
        if (segments.length == 0) {
            redirect(request, response, "/ko/home");
            return;
        }

        String lang = segments[0];
        if (!SUPPORTED_LANGUAGES.contains(lang)) {
            redirect(request, response, "/ko/home");
            return;
        }

        if (segments.length == 1) {
            redirect(request, response, "/" + lang + "/home");
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
}
