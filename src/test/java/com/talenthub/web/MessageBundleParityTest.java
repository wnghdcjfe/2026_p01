package com.talenthub.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class MessageBundleParityTest {

    @Test
    void englishAndKoreanMessageBundlesExposeTheSameKeys() throws IOException {
        Properties english = loadBundle("messages_en.properties");
        Properties korean = loadBundle("messages_ko.properties");

        assertThat(english.stringPropertyNames()).containsExactlyInAnyOrderElementsOf(korean.stringPropertyNames());
    }

    private Properties loadBundle(String name) throws IOException {
        Properties properties = new Properties();
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(name)) {
            assertThat(stream).as(name + " exists").isNotNull();
            properties.load(stream);
        }
        return properties;
    }
}
