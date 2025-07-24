package com.example.tasks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig
@TestPropertySource(properties = {
        "feature.flag=true",
        "app.name=LearningPlatform"
})
class InlinePropertiesTest {

    @Value("${feature.flag}")
    boolean featureFlag;

    @Value("${app.name}")
    String appName;

    @Test
    void testProperties() {
        assertTrue(featureFlag);
        assertEquals("LearningPlatform", appName);
    }
}
