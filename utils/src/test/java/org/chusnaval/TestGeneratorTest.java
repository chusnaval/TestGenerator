package org.chusnaval;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

class TestGeneratorTest {

    private TestGenerator generator;

    @Test
    void getRealOutputFolderWrongPathTest() {
        Assertions.assertEquals(".", TestGenerator.getRealOutputFolder(null));
        Assertions.assertEquals(".", TestGenerator.getRealOutputFolder(Path.of("")));
        Assertions.assertEquals(".", TestGenerator.getRealOutputFolder(Path.of("C:\\Foo\\")));
    }


    @Test
    void getRealOutputFolderOkPathTest() throws IOException {

        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path parentDir = fs.getPath("C:\\Foo");
        Files.createDirectory(parentDir);
        Assertions.assertEquals("C:\\Foo", TestGenerator.getRealOutputFolder(parentDir));
    }

    @Test
    void getRealOutputPackageTest() {
        Assertions.assertEquals("ttec.comple.entity.acceso", TestGenerator.getRealOutputPackage("F:\\testGen\\src\\ttec\\comple\\entity\\acceso\\CompleDAcAudInformes.java", null));
        Assertions.assertEquals("ttec.comple.entity.acceso.modified", TestGenerator.getRealOutputPackage("F:\\testGen\\src\\ttec\\comple\\entity\\acceso\\CompleDAcAudInformes.java", "ttec.comple.entity.acceso.modified"));
    }

    @Test
    void testFindAllFilesRecursive() throws IOException {
        Mockery context = new Mockery();
        String pathValue = "C:\\foo";
        final FinderService fileFinder = context.mock(FinderService.class);
        generator = new TestGenerator(true, fileFinder) {
            @Override
            public void generateTestFiles(String mainOption, String path, String outputFolder, String outputPackage) {

            }
        };

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
        generator = new TestGenerator(false, fileFinder) {
            @Override
            public void generateTestFiles(String mainOption, String path, String outputFolder, String outputPackage)  {

            }
        };

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
        generator = new TestGenerator(false, fileFinder) {
            @Override
            public void generateTestFiles(String mainOption, String path, String outputFolder, String outputPackage)  {

            }
        };

        context.checking(new Expectations() {{
            oneOf(fileFinder).obtainClassFromPath(Path.of(pathValue));
        }});

        generator.findAllFiles("class", pathValue);

        context.assertIsSatisfied();
    }

    @Test
    void obtainClassName() {
        Assertions.assertEquals("ComplePAcAplicacionesId", TestGenerator.obtainClassName("org\\chusnaval\\main\\entity\\acceso\\param\\id\\ComplePAcAplicacionesId.java"));
    }

}
