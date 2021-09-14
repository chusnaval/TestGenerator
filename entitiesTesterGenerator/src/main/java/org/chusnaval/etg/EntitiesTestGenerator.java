package org.chusnaval.etg;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.chusnaval.FinderService;
import org.chusnaval.GeneratorException;
import org.chusnaval.TestGenerator;
import org.chusnaval.TypeTestCalculator;
import org.junit.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntitiesTestGenerator extends TestGenerator {

    /**
     * Default constructor
     *
     * @param recursiveMode Establish the recursive mode
     */
    public EntitiesTestGenerator(boolean recursiveMode, FinderService finder) {
        super(recursiveMode, finder);
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
    @Override
    public void generateTestFiles(String mainOption, String path, String outputFolder, String outputPackage) throws IOException, GeneratorException {

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

    protected void generateTestFile(String classFile, Map<String, String> propertiesNames, String outputFolder,
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




}
