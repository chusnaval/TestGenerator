package org.chusnaval;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.chusnaval.TypeTestCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class TypeTestCalculatorTest {


    @Test
    void createEmptyClassTest() {

        TypeSpec typeSpec = TypeTestCalculator.getTypeSpec("ComplePAcAplicacionesId", new ArrayList<>());

        String expectedResult = "public final class ComplePAcAplicacionesIdTest {\n}\n";

        assertThat(typeSpec.toString(), equalTo(expectedResult));
    }

    @Test
    void createClassWithAMethodTest() {
        String capitalizedPropertyName = StringUtils.capitalize("cdAplicacion");
        MethodSpec.Builder builder = MethodSpec.methodBuilder("testSetter" + capitalizedPropertyName)
                .addModifiers(Modifier.PUBLIC)
                .addException(NoSuchFieldException.class)
                .addException(IllegalAccessException.class)
                .addCode("\tfinal ComplePAcAplicacionesId pojo = new ComplePAcAplicacionesId();\n\t" + "pojo.set"
                        + capitalizedPropertyName + "(TESTING_STRING);\n\n"
                        + "final Field field = pojo.getClass().getDeclaredField(\"cdAplicacion\");\n"
                        + "field.setAccessible(true);\n\n" + "assertEquals(FIELDS_DIDNT_MATCH, TESTING_STRING, field.get(pojo));" + "\n")
                .addAnnotation(AnnotationSpec.builder(ClassName.get(org.junit.Test.class))
                        .build());

        List<MethodSpec> methods = new ArrayList<>();
        methods.add(builder.build());
        TypeSpec typeSpec = TypeTestCalculator.getTypeSpec("ComplePAcAplicacionesId", methods);

        String expectedResult = "public final class ComplePAcAplicacionesIdTest {\n  @org.junit.Test\n  public void testSetterCdAplicacion() throws java.lang.NoSuchFieldException,\n      java.lang.IllegalAccessException {\n    \tfinal ComplePAcAplicacionesId pojo = new ComplePAcAplicacionesId();\n    \tpojo.setCdAplicacion(TESTING_STRING);\n\n    final Field field = pojo.getClass().getDeclaredField(\"cdAplicacion\");\n    field.setAccessible(true);\n\n    assertEquals(FIELDS_DIDNT_MATCH, TESTING_STRING, field.get(pojo));\n  }\n}\n";

        assertThat(typeSpec.toString(), equalTo(expectedResult));
    }
}
