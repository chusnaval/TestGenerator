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
    void testFindAllFilesRecursive() throws IOException {
        Mockery context = new Mockery();
        String pathValue = "C:\\foo";
        final FinderService fileFinder = context.mock(FinderService.class);
        generator = new EntitiesTestGenerator(true, fileFinder);

        context.checking(new Expectations() {{
            oneOf(fileFinder).obtainClassesRecursivePath(Path.of(pathValue));
        }});

        generator.findAllFiles("dir", pathValue);

        context.assertIsSatisfied();
    }

    @Test
    void testFindAllFilesNoRecursiveInDir() throws IOException {
        Mockery context = new Mockery();
        String pathValue = "C:\\foo";
        final FinderService fileFinder = context.mock(FinderService.class);
        generator = new EntitiesTestGenerator(false, fileFinder);

        context.checking(new Expectations() {{
            oneOf(fileFinder).obtainClassesFromPath(Path.of(pathValue));
        }});

        generator.findAllFiles("dir", pathValue);

        context.assertIsSatisfied();
    }

    @Test
    void testFindAllFilesNoRecursiveInClass() throws IOException {
        Mockery context = new Mockery();
        String pathValue = "C:\\foo";
        final FinderService fileFinder = context.mock(FinderService.class);
        generator = new EntitiesTestGenerator(false, fileFinder);

        context.checking(new Expectations() {{
            oneOf(fileFinder).obtainClassFromPath(Path.of(pathValue));
        }});

        generator.findAllFiles("class", pathValue);

        context.assertIsSatisfied();
    }

    @Test
    void obtainClassName() {
        Assertions.assertEquals("ComplePAcAplicacionesId", EntitiesTestGenerator.obtainClassName("org\\chusnaval\\main\\entity\\acceso\\param\\id\\ComplePAcAplicacionesId.java"));
    }

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

    @Test
    void getRealOutputFolderWrongPathTest() {
        Assertions.assertEquals(".", EntitiesTestGenerator.getRealOutputFolder(null));
        Assertions.assertEquals(".", EntitiesTestGenerator.getRealOutputFolder(Path.of("")));
        Assertions.assertEquals(".", EntitiesTestGenerator.getRealOutputFolder(Path.of("C:\\Foo\\")));
    }


    @Test
    void getRealOutputFolderOkPathTest() throws IOException {

        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path parentDir = fs.getPath("C:\\Foo");
        Files.createDirectory(parentDir);
        Assertions.assertEquals("C:\\Foo", EntitiesTestGenerator.getRealOutputFolder(parentDir));
    }

    @Test
    void getRealOutputPackageTest() {
        Assertions.assertEquals("ttec.comple.entity.acceso", EntitiesTestGenerator.getRealOutputPackage("F:\\testGen\\src\\ttec\\comple\\entity\\acceso\\CompleDAcAudInformes.java", null));
        Assertions.assertEquals("ttec.comple.entity.acceso.modified", EntitiesTestGenerator.getRealOutputPackage("F:\\testGen\\src\\ttec\\comple\\entity\\acceso\\CompleDAcAudInformes.java", "ttec.comple.entity.acceso.modified"));
    }
}

