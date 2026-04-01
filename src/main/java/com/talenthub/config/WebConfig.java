package com.talenthub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

@Configuration
public class WebConfig {

    @Bean(name = "localeResolver")
    public LocaleResolver localeResolver() {
        return new PathLocaleResolver();
    }
}
