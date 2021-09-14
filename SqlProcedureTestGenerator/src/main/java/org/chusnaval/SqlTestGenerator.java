package org.chusnaval;

import com.squareup.javapoet.*;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SqlTestGenerator {

    private final boolean recursiveMode;

    private final FinderService fileFinder;

    private static final String CLASS_FILE = "class";

    private static final String DIRECTORY = "dir";

    /**
     * Default constructor
     *
     * @param recursiveMode Establish the recursive mode
     */
    public SqlTestGenerator(boolean recursiveMode, FinderService finder) {
        this.recursiveMode = recursiveMode;
        this.fileFinder = finder;
    }


    public static MethodSpec getExecuteMethodSpec(String className, List<ParameterType> inputParamNames, List<ParameterType> outputParamNames, String functionName) throws GeneratorException {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("testExecute")
                .addModifiers(Modifier.PUBLIC)
                .addException(Exception.class)
                .addStatement("given(callableStatement.execute()).willReturn(false)")
                .addStatement("given(callableStatement.getUpdateCount()).willReturn(-1)");

                for(ParameterType entry : outputParamNames){
                    builder.addStatement("given(callableStatement.getObject(1)).willReturn(" + entry.getParameter() + ")");
                }

                builder.addStatement("given(connection.prepareCall(\n" +
                        "                \"" + functionName + "\"))\n" +
                        "                        .willReturn(callableStatement)")
                .addStatement(className + " procedure = new " + className + "(dataSource)");

                builder.addStatement(calculateSqlProcedureCallStatement(inputParamNames));

                int parameterPosition = 2; // starts in 2 because 1 is outputparameter
                for(ParameterType entry : inputParamNames){
                    if("String".equals(entry.getType())){
                        builder.addStatement(calculateStringParameter(parameterPosition, entry.getParameter()));
                    }else if("NUMERIC".equals(entry.getType())){
                        builder.addStatement(calculateNumericParameter(parameterPosition, entry.getParameter()));
                    }else{
                        throw new GeneratorException("SQL Type not implemented");
                    }

                    parameterPosition++;
                }

                for(ParameterType entry : outputParamNames){
                    builder.addStatement("verify(callableStatement).registerOutParameter(" + entry.getParameter() + ", Types." + entry.getType() + ")")
                    .addStatement("assertEquals(\"" + entry.getParameter() + "\", resultado)");
                }
                builder.addAnnotation(AnnotationSpec.builder(ClassName.get(Test.class))
                        .build());
        return builder.build();
    }

    public static String calculateStringParameter(int parameterPosition, String parameter) {
        return "verify(callableStatement).setString(" + parameterPosition + ", \"" + parameter + "\")";
    }

    public static String calculateNumericParameter(int parameterPosition, String parameter) {
        return "verify(callableStatement).setObject(" + parameterPosition + ", \"" + parameter + "\", Types.NUMERIC)";
    }

    private void generateTestFile(String classFile, String outputFolder,
                                  String outputPackage) throws IOException {

        String className = obtainClassName(classFile);
        List<MethodSpec> methods = new ArrayList<>();
        //methods.add(getExecuteMethodSpec(className, inputParamNames, outputParamNames, functionName));
        String testClassName = className + "Test";
        TypeSpec typeSpec = TypeTestCalculator.getTypeSpec(testClassName, methods);
        String realOutputPackage = getRealOutputPackage(classFile, outputPackage);
        JavaFile javaFile = JavaFile.builder(realOutputPackage, typeSpec)
                .addStaticImport(Assert.class, "assertEquals")
                .addStaticImport(BDDMockito.class, "given")
                .addStaticImport(BDDMockito.class, "atLeastOnce")
                .addStaticImport(BDDMockito.class, "mock")
                .addStaticImport(BDDMockito.class, "verify")
                .build();

        String realOutputFolder = getRealOutputFolder(Path.of(outputFolder));
        javaFile.writeTo(new File(realOutputFolder));

    }

    public static String calculateSqlProcedureCallStatement(List<ParameterType> paramNames) {
        StringBuilder builder = new StringBuilder("String resultado = procedure.execute(");
        int index = 0;
        for(ParameterType entry : paramNames){
            builder.append("\"").append(entry.getParameter()).append("\"");
            index++;
            if(index<paramNames.size()){
                builder.append(", ");
            }

        }
        builder.append(")");
        return builder.toString();
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
            generateTestFile(classFile, outputFolder, outputPackage);
        }
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

}
