package org.chusnaval.etg;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.squareup.javapoet.MethodSpec;
import org.chusnaval.FileFinder;
import org.chusnaval.FinderService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;


class EntitiesTestGeneratorTest {


    private EntitiesTestGenerator generator;


    @Test
    void getMethodSpecsTest() {
        Map<String, String> propertiesNames = new HashMap<>();
        propertiesNames.put("cdAplicacion", "String");
        propertiesNames.put("serialVersionUID", "String");
        List<MethodSpec> methods = EntitiesTestGenerator.getMethodSpecs(propertiesNames, "ComplePAcAplicacionesId");
        Assertions.assertEquals(2, methods.size());
        String expectedSetterResult = "@org.junit.Test\n" +
                "public void testSetterCdAplicacion() throws java.lang.NoSuchFieldException,\n    java.lang.IllegalAccessException {\n  \t" +
                "final ComplePAcAplicacionesId pojo = new ComplePAcAplicacionesId();\n  \t" +
                "pojo.setCdAplicacion(TESTING_STRING);\n\n  " +
                "final Field field = pojo.getClass().getDeclaredField(\"cdAplicacion\");\n  " +
                "field.setAccessible(true);\n\n  " +
                "assertEquals(FIELDS_DIDNT_MATCH, TESTING_STRING, field.get(pojo));\n" +
                "}\n";
        assertThat(methods.get(0).toString(), equalTo(expectedSetterResult));

        String expectedGetterResult = "@org.junit.Test\n" +
                "public void testGetterCdAplicacion() throws java.lang.NoSuchFieldException,\n    java.lang.IllegalAccessException {\n  \t" +
                "final ComplePAcAplicacionesId pojo = new ComplePAcAplicacionesId();\n  \t" +
                "final Field field = pojo.getClass().getDeclaredField(\"cdAplicacion\");\n  " +
                "field.setAccessible(true);\n\n  " +
                "field.set(pojo,TESTING_STRING);\n\n  " +
                "assertEquals(FIELDS_WASNT_RETRIEVED_PROPERLY, TESTING_STRING, (String) pojo.getCdAplicacion());\n" +
                "}\n";
        assertThat(methods.get(1).toString(), equalTo(expectedGetterResult));
    }


}

