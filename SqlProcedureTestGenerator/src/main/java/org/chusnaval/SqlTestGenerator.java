package org.chusnaval;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.Statement;
import com.squareup.javapoet.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.lang.model.element.Modifier;
import java.io.*;
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
        for (String classFile : findAllFiles(mainOption, path)) {
            // TODO we need to verify if the classFile extends ttec.comple.procedures.procesos.StoredProcedure
            generateTestFile(classFile,  outputFolder, outputPackage);
        }
    }


    public static MethodSpec getExecuteMethodSpec(String className, List<ParameterType> inputParamNames, List<ParameterType> outputParamNames, List<ParameterType> procedureParamNames, String functionName) throws GeneratorException {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("testExecute")
                .addModifiers(Modifier.PUBLIC)
                .addException(Exception.class)
                .addStatement("given(callableStatement.execute()).willReturn(false)")
                .addStatement("given(callableStatement.getUpdateCount()).willReturn(-1)");

                for(ParameterType entry : outputParamNames){
                    builder.addStatement("given(callableStatement.getObject(1)).willReturn(" + entry.getParameter() + ")");
                }

                builder.addStatement("given(connection.prepareCall(\n" +
                        "                \"{? = call " + functionName + "(" + addFakeParameter(inputParamNames.size()) + ")}\"))\n" +
                        "                        .willReturn(callableStatement)")
                .addStatement(className + " procedure = new " + className + "(dataSource)");

                builder.addStatement(calculateSqlProcedureCallStatement(inputParamNames));

                int parameterPosition = 2; // starts in 2 because 1 is outputparameter
                for(ParameterType entry : procedureParamNames){
                    if("VARCHAR".equals(entry.getType())){
                        builder.addStatement(calculateStringParameter(parameterPosition, inputParamNames.get(parameterPosition-2).getParameter()));
                    }else if("NUMERIC".equals(entry.getType()) || "INTEGER".equals(entry.getType())){
                        builder.addStatement(calculateNumericParameter(parameterPosition, inputParamNames.get(parameterPosition-2).getParameter(), entry.getType()));
                    }else{
                        System.out.println("error en clase: " + className + " tipo: " + entry.getType());
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

    private static String addFakeParameter(int limit) {
        StringBuilder result = new StringBuilder();
        for(int index = 0; index < limit; index++){
            result.append("?");
            if(index+1<limit){
                result.append(", ");
            }
        }
        return result.toString();
    }

    public static String calculateStringParameter(int parameterPosition, String parameter) {
        return "verify(callableStatement).setString(" + parameterPosition + ", \"" + parameter + "\")";
    }

    public static String calculateNumericParameter(int parameterPosition, String parameter, String sqlType) {
        return "verify(callableStatement).setObject(" + parameterPosition + ", \"" + parameter + "\", Types." + sqlType + ")";
    }

    protected void generateTestFile(String classFile, String outputFolder,
                                  String outputPackage) throws IOException, GeneratorException {

        String className = obtainClassName(classFile);
        List<MethodSpec> methods = new ArrayList<>();
        methods.add(getSetUp());
        methods.add(getVerifyClosed());
        methods.add(getExecuteMethodSpec(className, obtainInputParameters(classFile), obtaintOutputParameters(classFile), obtaintProcedureParameters(classFile), obtainFunctionName(classFile)));
        TypeSpec typeSpec = TypeTestCalculator.getTypeSpec(className, methods);

        String realOutputPackage = getRealOutputPackage(classFile, outputPackage);
        JavaFile javaFile = JavaFile.builder(realOutputPackage, typeSpec)
                .addStaticImport(Assert.class, "assertEquals")
                .addStaticImport(Mockito.class, "given")
                .addStaticImport(Mockito.class, "atLeastOnce")
                .addStaticImport(Mockito.class, "mock")
                .addStaticImport(Mockito.class, "verify")
                .build();

        String realOutputFolder = getRealOutputFolder(Path.of(outputFolder));
        javaFile.writeTo(new File(realOutputFolder));

    }

    private MethodSpec getSetUp() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("setup")
                .addModifiers(Modifier.PUBLIC)
                .addException(Exception.class)
                .addStatement("dataSource = mock(DataSource.class)")
                .addStatement("connection = mock(Connection.class)")
                .addStatement("callableStatement = mock(CallableStatement.class)")
                .addStatement("given(dataSource.getConnection()).willReturn(connection)")
                .addStatement("given(callableStatement.getConnection()).willReturn(connection)");
        builder.addAnnotation(AnnotationSpec.builder(ClassName.get(Before.class))
                .build());
        return builder.build();
    }

    private MethodSpec getVerifyClosed() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("verifyClosed")
                .addModifiers(Modifier.PUBLIC)
                .addException(Exception.class)
                .addStatement("verify(callableStatement).close()")
                .addStatement("verify(connection, atLeastOnce()).close()");
        builder.addAnnotation(AnnotationSpec.builder(ClassName.get(After.class))
                .build());
        return builder.build();
    }

    private List<ParameterType> obtaintProcedureParameters(String classFile) throws IOException {
        List<ParameterType> collections = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();
        try (InputStream is = new FileInputStream(classFile)) {
            CompilationUnit cu = StaticJavaParser.parse(is);

            ConstructorStatementCollector fnc = new ConstructorStatementCollector();
            fnc.visit(cu, statements);
            for(Statement st : statements){
                if(st.toString().contains("new SqlParameter")){
                    collections.add(new ParameterType(st.toString().substring(st.toString().indexOf('"')+1, st.toString().lastIndexOf('"')), st.toString().substring(st.toString().indexOf("Types.")+6, st.toString().length()-3)));
                }
            }
        }

        return collections;
    }

    protected String obtainFunctionName(String classFile) throws IOException {
        String functionName = "";
        String constantName = "";
        List<String> constants = new ArrayList<>();
        List<Statement> collections = new ArrayList<>();
        try (InputStream is = new FileInputStream(classFile)) {
            CompilationUnit cu = StaticJavaParser.parse(is);
           ConstructorStatementCollector fnc = new ConstructorStatementCollector();
            fnc.visit(cu, collections);
            for(Statement st : collections){
                if(st.toString().contains("super(")){
                    constantName = st.toString().substring(st.toString().indexOf("super(")+17, st.toString().length()-2);
                    cu.accept(new ClassVisitor(), constants);

                    for(String constant : constants){
                        if(constant.contains(constantName)){
                            functionName = constant.substring(constant.indexOf('"')+1, constant.length()-2).trim();
                        }
                    }
                }
            }

        }
        return functionName;
    }

    private List<ParameterType> obtaintOutputParameters(String classFile) throws IOException {

        List<ParameterType> collections = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();
        try (InputStream is = new FileInputStream(classFile)) {
            CompilationUnit cu = StaticJavaParser.parse(is);
            ConstructorStatementCollector fnc = new ConstructorStatementCollector();
            fnc.visit(cu, statements);
            for(Statement st : statements){
                if(st.toString().contains("new SqlOutParameter")){
                    collections.add(new ParameterType("1", st.toString().substring(st.toString().indexOf("Types.")+6, st.toString().length()-3)));
                }
            }
        }

        return collections;
    }

    private List<ParameterType> obtainInputParameters(String classFile) throws IOException {
        List<ParameterType> collections = new ArrayList<>();
        try (InputStream is = new FileInputStream(classFile)) {
            CompilationUnit cu = StaticJavaParser.parse(is);
            InputTypeCollector fnc = new InputTypeCollector();
            fnc.visit(cu, collections);
        }

        return collections;
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
