package org.chusnaval;

import com.squareup.javapoet.*;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SqlTestGenerator extends TestGenerator{

       /**
     * Default constructor
     *
     * @param recursiveMode Establish the recursive mode
     */
    public SqlTestGenerator(boolean recursiveMode, FinderService finder) {
        super(recursiveMode, finder);
    }

    @Override
    public void generateTestFiles(String mainOption, String path, String outputFolder, String outputPackage) throws IOException, GeneratorException {

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

    protected void generateTestFile(String classFile, String outputFolder,
                                  String outputPackage) throws IOException, GeneratorException {

        String className = obtainClassName(classFile);
        List<MethodSpec> methods = new ArrayList<>();
        methods.add(getExecuteMethodSpec(className, obtainInputParameters(classFile), obtaintOutputParameters(classFile), obtainFunctionName(classFile)));
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

    private String obtainFunctionName(String classFile) {
        return null;
    }

    private List<ParameterType> obtaintOutputParameters(String classFile) {
        return null;
    }

    private List<ParameterType> obtainInputParameters(String classFile) {
        return null;
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



}
