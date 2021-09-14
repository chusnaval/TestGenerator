package org.chusnaval.etg;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValueFinderServiceTest {

    @Test
    void testIntegerValues() {

        String expectedValue = "TESTING_INTEGER";
        Assertions.assertEquals(expectedValue, ValueFinderService.getValueByType("int"));
        Assertions.assertEquals(expectedValue, ValueFinderService.getValueByType("byte"));
        Assertions.assertEquals(expectedValue, ValueFinderService.getValueByType("short"));
        Assertions.assertEquals(expectedValue, ValueFinderService.getValueByType("Integer"));

    }

    @Test
    void testLongValues() {

        String expectedValue = "TESTING_LONG";
        Assertions.assertEquals(expectedValue, ValueFinderService.getValueByType("long"));
        Assertions.assertEquals(expectedValue, ValueFinderService.getValueByType("Long"));
    }

    @Test
    void testDecimalValues() {

        String expectedValue = "TESTING_FLOAT";
        Assertions.assertEquals(expectedValue, ValueFinderService.getValueByType("float"));
        Assertions.assertEquals(expectedValue, ValueFinderService.getValueByType("double"));
        Assertions.assertEquals(expectedValue, ValueFinderService.getValueByType("Float"));
        Assertions.assertEquals(expectedValue, ValueFinderService.getValueByType("Double"));
    }

    @Test
    void testLogicalValues() {

        Assertions.assertEquals(true, ValueFinderService.getValueByType("boolean"));
        Assertions.assertEquals(true, ValueFinderService.getValueByType("Boolean"));
    }

    @Test
    void testStringValues() {

        Assertions.assertEquals("TESTING_STRING", ValueFinderService.getValueByType("String"));
    }

    @Test
    void testDateValues() {

        Assertions.assertEquals("TESTING_DATE", ValueFinderService.getValueByType("Date"));
    }
}
