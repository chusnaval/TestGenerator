package org.chusnaval.etg;

import com.squareup.javapoet.MethodSpec;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


class MethodTestCalculatorTest {

    @Test
    void createSetterTest() {

        MethodSpec setterMethodSpec = MethodTestCalculator.obtainSetter("ComplePAcAplicacionesId", "cdAplicacion", "TESTING_STRING");

        String expectedResult = "@org.junit.Test\n" +
                "public void testSetterCdAplicacion() throws java.lang.NoSuchFieldException,\n    java.lang.IllegalAccessException {\n  \t" +
                "final ComplePAcAplicacionesId pojo = new ComplePAcAplicacionesId();\n  \t" +
                "pojo.setCdAplicacion(TESTING_STRING);\n\n  " +
                "final Field field = pojo.getClass().getDeclaredField(\"cdAplicacion\");\n  " +
                "field.setAccessible(true);\n\n  " +
                "assertEquals(FIELDS_DIDNT_MATCH, TESTING_STRING, field.get(pojo));\n" +
                "}\n";

        assertThat(setterMethodSpec.toString(), equalTo(expectedResult));
    }


    @Test
    void createGetterTest() {

        MethodSpec getterMethodSpec = MethodTestCalculator.obtainGetter("ComplePAcAplicacionesId", "cdAplicacion", "TESTING_STRING", "String");

        String expectedResult = "@org.junit.Test\n" +
                "public void testGetterCdAplicacion() throws java.lang.NoSuchFieldException,\n    java.lang.IllegalAccessException {\n  \t" +
                "final ComplePAcAplicacionesId pojo = new ComplePAcAplicacionesId();\n  \t" +
                "final Field field = pojo.getClass().getDeclaredField(\"cdAplicacion\");\n  " +
                "field.setAccessible(true);\n\n  " +
                "field.set(pojo,TESTING_STRING);\n\n  " +
                "assertEquals(FIELDS_WASNT_RETRIEVED_PROPERLY, TESTING_STRING, (String) pojo.getCdAplicacion());\n" +
                "}\n";

        assertThat(getterMethodSpec.toString(), equalTo(expectedResult));
    }

    @Test
    void createEqualsTest() {

        MethodSpec getterMethodSpec = MethodTestCalculator.obtainEquals("ComplePAcAplicacionesId");

        String expectedResult = "@org.junit.Test\n" +
                "public void equalsContract() {\n  \t" +
                "EqualsVerifier.forClass(ComplePAcAplicacionesId.class).verify();\n" +
                "}\n";

        assertThat(getterMethodSpec.toString(), equalTo(expectedResult));
    }
}
