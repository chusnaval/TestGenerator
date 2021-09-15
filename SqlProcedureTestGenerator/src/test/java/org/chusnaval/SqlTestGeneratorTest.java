package org.chusnaval;

import com.squareup.javapoet.MethodSpec;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class SqlTestGeneratorTest {

    private List<ParameterType> inputParamNames;
    private List<ParameterType> outputParamNames;
    private List<ParameterType> procedureParamNames;

    @BeforeEach
    public void init(){
        outputParamNames = new ArrayList<>();
        outputParamNames.add(new ParameterType("1", "VARCHAR"));

        procedureParamNames = new ArrayList<>();
        procedureParamNames.add(new ParameterType("p_campana", "NUMERIC"));
        procedureParamNames.add(new ParameterType("p_dpe_docident", "String"));
        procedureParamNames.add(new ParameterType("p_dpe_cdnumexp", "String"));
        procedureParamNames.add(new ParameterType("p_ambito", "String"));

        inputParamNames = new ArrayList<>();
        inputParamNames.add(new ParameterType("2015", "String"));
        inputParamNames.add(new ParameterType("52552291M", "String"));
        inputParamNames.add(new ParameterType("6000200", "String"));
        inputParamNames.add(new ParameterType("01010000001000000700000000032300", "String"));
    }

    @Test
    void getMethodSpecsTest() throws GeneratorException {

        // TODO comprobar que es una funcion o un procedure
        // TODO comprobar que parametros tiene y de que tipo
        MethodSpec method = SqlTestGenerator.getExecuteMethodSpec("AccAmbitosGst", inputParamNames, outputParamNames, procedureParamNames, "{? = call COMPLE_K_DATSGA.COMPLE_F_CHK_AMBITO(?, ?, ?, ?)}");
        String expectedSetterResult = "@org.junit.Test\n" +
                "public void testExecute() throws java.lang.Exception {\n  " +
                "given(callableStatement.execute()).willReturn(false);\n  " +
                "given(callableStatement.getUpdateCount()).willReturn(-1);\n  " +
                "given(callableStatement.getObject(1)).willReturn(1);\n  " +
                "given(connection.prepareCall(\n" +
                "                      \"{? = call COMPLE_K_DATSGA.COMPLE_F_CHK_AMBITO(?, ?, ?, ?)}\"))\n" +
                "                              .willReturn(callableStatement);\n  " +
                "AccAmbitosGst procedure = new AccAmbitosGst(dataSource);\n  " +
                "String resultado = procedure.execute(\"2015\", \"52552291M\", \"6000200\", \"01010000001000000700000000032300\");\n  " +
                "verify(callableStatement).setObject(2, \"2015\", Types.NUMERIC);\n  " +
                "verify(callableStatement).setString(3, \"52552291M\");\n  " +
                "verify(callableStatement).setString(4, \"6000200\");\n  " +
                "verify(callableStatement).setString(5, \"01010000001000000700000000032300\");\n  " +
                "verify(callableStatement).registerOutParameter(1, Types.VARCHAR);\n  " +
                "assertEquals(\"1\", resultado);\n"+
                "}\n";
        assertThat(method.toString(), equalTo(expectedSetterResult));


    }

    @Test
    void calculateStringParameterTest(){

        assertThat(SqlTestGenerator.calculateStringParameter(3, "52552291M"), equalTo("verify(callableStatement).setString(3, \"52552291M\")"));
        assertThat(SqlTestGenerator.calculateStringParameter(4, "6000200"), equalTo("verify(callableStatement).setString(4, \"6000200\")"));
        assertThat(SqlTestGenerator.calculateStringParameter(5, "01010000001000000700000000032300"), equalTo("verify(callableStatement).setString(5, \"01010000001000000700000000032300\")"));
    }

    @Test
    void calculateNumericParameterTest(){

        assertThat(SqlTestGenerator.calculateNumericParameter(2, "2015", "NUMERIC"), equalTo("verify(callableStatement).setObject(2, \"2015\", Types.NUMERIC)"));
    }

    @Test
    void calculateSqlProcedureCallStatementTest(){

        assertThat(SqlTestGenerator.calculateSqlProcedureCallStatement(inputParamNames), equalTo("String resultado = procedure.execute(\"2015\", \"52552291M\", \"6000200\", \"01010000001000000700000000032300\")"));

    }

}
