package org.chusnaval.etg;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.chusnaval.FinderService;
import org.junit.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntitiesTestGenerator {

    private final boolean recursiveMode;

    private final FinderService fileFinder;

    private static final String CLASS_FILE = "class";

    private static final String DIRECTORY = "dir";

    /**
     * Default constructor
     *
     * @param recursiveMode Establish the recursive mode
     */
    public EntitiesTestGenerator(boolean recursiveMode, FinderService finder) {
        this.recursiveMode = recursiveMode;
        this.fileFinder = finder;
    }

    /**
     * Generates the test files to the proper output folder
     *
     * @param mainOption    define if generation is about a class or directory
     * @param path          path where classes are
     * @param outputFolder  dir were we write the result
     * @param outputPackage package to write in test
     * @throws IOException exception where the route not exists or is not accessible
     */
    public void generateTestFiles(String mainOption, String path, String outputFolder, String outputPackage) throws IOException {

        for (String classFile : findAllFiles(mainOption, path)) {
            Map<String, String> propertiesNames = extractProperties(classFile);
            generateTestFile(classFile, propertiesNames, outputFolder, outputPackage);
        }
    }

    /**
     * Extract all properties from a class file
     *
     * @param classFile class file to parse
     * @return the properties name
     * @throws IOException error if dir not exits or is not accessible
     */
    protected Map<String, String> extractProperties(String classFile) throws IOException {
        Map<String, String> propertiesNames = new HashMap<>();
        try (InputStream is = new FileInputStream(classFile)) {
            CompilationUnit cu = StaticJavaParser.parse(is);
            FieldNameCollector fnc = new FieldNameCollector();
            fnc.visit(cu, propertiesNames);
        }
        return propertiesNames;
    }

    private void generateTestFile(String classFile, Map<String, String> propertiesNames, String outputFolder,
                                  String outputPackage) throws IOException {

        String className = obtainClassName(classFile);
        ClassName constants = ClassName.get("ttec.comple.util", "TestConstantes");
        ClassName fieldClass = ClassName.get("java.lang.reflect", "Field");
        List<MethodSpec> methods = getMethodSpecs(propertiesNames, className);
        methods.add(MethodTestCalculator.obtainEquals(className));
        TypeSpec typeSpec = TypeTestCalculator.getTypeSpec(className, methods);
        String realOutputPackage = getRealOutputPackage(classFile, outputPackage);
        JavaFile javaFile = JavaFile.builder(realOutputPackage, typeSpec)
                .addStaticImport(Assert.class, "assertEquals")
                .addStaticImport(Assert.class, "assertNotNull")
                .addStaticImport(constants, "TESTING_INTEGER")
                .addStaticImport(constants, "TESTING_LONG")
                .addStaticImport(constants, "TESTING_FLOAT")
                .addStaticImport(constants, "TESTING_STRING")
                .addStaticImport(constants, "TESTING_DATE")
                .addStaticImport(constants, "FIELDS_DIDNT_MATCH")
                .addStaticImport(constants, "FIELDS_WASNT_RETRIEVED_PROPERLY")
                .addStaticImport(fieldClass, "*")
                .build();

        String realOutputFolder = getRealOutputFolder(Path.of(outputFolder));
        javaFile.writeTo(new File(realOutputFolder));

    }

    /**
     * Get the real output folder, if is empty or null we use app folder as destination
     *
     * @param outputFolder output folder indicated from user
     * @return real output folder
     */
    protected static String getRealOutputFolder(Path outputFolder) {
        String destinationDir = ".";
        if (outputFolder != null && !outputFolder.toString().isEmpty() && Files.isDirectory(outputFolder)) {
            destinationDir = outputFolder.toString();
        }
        return destinationDir;
    }

    /**
     * Obtain the class name from a Java File
     *
     * @param inputFile input java file
     * @return class name
     */
    protected static String obtainClassName(String inputFile) {
        return inputFile.substring(inputFile.lastIndexOf('\\') + 1, inputFile.indexOf("."));
    }

    /**
     * Gets the real output package
     *
     * @param classFile     class path file
     * @param outputPackage output package to write tests
     * @return the real output package
     */
    protected static String getRealOutputPackage(String classFile, String outputPackage) {
        int displacement = 4;
        String realOutputPackage = classFile.contains("src")
                ? classFile.substring(classFile.indexOf("src") + displacement, classFile.lastIndexOf('\\')).replace('\\', '.')
                : "";
        if (outputPackage != null && !outputPackage.isEmpty()) {
            realOutputPackage = outputPackage;
        }
        return realOutputPackage;
    }

    /**
     * Create the getter and setter method spec from a map of properties
     *
     * @param propertiesNames properties names
     * @param className       class name where the properties are
     * @return a list of getter and setter method specs
     */
    protected static List<MethodSpec> getMethodSpecs(Map<String, String> propertiesNames, String className) {
        List<MethodSpec> methods = new ArrayList<>();
        for (Map.Entry<String, String> propertyName : propertiesNames.entrySet()) {

            Object value = ValueFinderService.getValueByType(propertyName.getValue());

            if (value != null && !"serialVersionUID".equals(propertyName.getKey())) { // TODO actually not works with own classes
                methods.add(MethodTestCalculator.obtainSetter(className, propertyName.getKey(), value));
                methods.add(MethodTestCalculator.obtainGetter(className, propertyName.getKey(), value, propertyName.getValue()));
            }

        }
        return methods;
    }

    /**
     * Obtain all files from a path
     *
     * @param value     establish if we find in a dir or a single class
     * @param pathValue the path where are the files
     * @return list of all files
     * @throws IOException error if path not exists or is not accessible
     */
    protected List<String> findAllFiles(String value, String pathValue) throws IOException {
        List<String> files = null;

        if (recursiveMode) {
            files = fileFinder.obtainClassesRecursivePath(Path.of(pathValue));
        } else {
            if (value.equals(DIRECTORY)) {
                files = fileFinder.obtainClassesFromPath(Path.of(pathValue));
            } else if (value.equals(CLASS_FILE)) {
                files = fileFinder.obtainClassFromPath(Path.of(pathValue));
            }
        }
        return files;
    }


}
