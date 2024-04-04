package com.example.demo.ey.utils;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.demo.ey.utils.UtilsServices.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UtilsServicesTest {
    private static final String EMAIL_GOOD = "email@domain.org";
    private static final String EMAIL_BAD = "emaildomain.org";
    private static final String REGEX_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    private static final Long TIME = 1700150751269L;

    @Test
    void validateValueWithRegex_Successful_Test() {
        assertTrue(validateValueWithRegex(EMAIL_GOOD, REGEX_PATTERN));
    }

    @Test
    void validateValueWithRegex_Fail_Test() {
        assertFalse(validateValueWithRegex(EMAIL_BAD, REGEX_PATTERN));
    }

    @Test
    void covertDateStr_Successful_Test() {
        String date = "16-11-2023 13:05:51";
        assertEquals(date, covertDateStr(TIME));
    }

    @Test
    void generateUUID_Test() {
        assertEquals(36, generateUUID().length());
    }

}
