package com.example.tasks;

import com.example.tasks.FlagHolder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = FlagHolder.class)
class FlagHolderTest {

    @Autowired
    FlagHolder flagHolder;

    @Test
    public void testEnabledFlag(){
        flagHolder.enableFlag();

        assertTrue(flagHolder.isFlagEnabled());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    public void testFlagShouldBeFalse(){
        assertFalse(flagHolder.isFlagEnabled());
    }

}