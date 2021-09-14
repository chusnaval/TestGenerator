package org.chusnaval.etg;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.junit.Test;
import org.springframework.util.StringUtils;

import javax.lang.model.element.Modifier;

public class MethodTestCalculator {

    /**
     * Default constructor
     */
    private MethodTestCalculator() {
        super();
    }

    /**
     * Create a test for setter method in a class
     *
     * @param className    Class name to be tested
     * @param propertyName property name that has a setter to test
     * @param valueByType  value to test the setter
     * @return MethodSpec with a test for the setter
     */
    public static MethodSpec obtainSetter(String className, String propertyName, Object valueByType) {
        String capitalizedPropertyName = StringUtils.capitalize(propertyName);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("testSetter" + capitalizedPropertyName)
                .addModifiers(Modifier.PUBLIC)
                .addException(NoSuchFieldException.class)
                .addException(IllegalAccessException.class)
                .addCode("\tfinal " + className + " pojo = new " + className + "();\n\t" + "pojo.set"
                        + capitalizedPropertyName + "(" + valueByType + ");\n\n"
                        + "final Field field = pojo.getClass().getDeclaredField(\"" + propertyName + "\");\n"
                        + "field.setAccessible(true);\n\n" + "assertEquals(FIELDS_DIDNT_MATCH, " + valueByType + ", field.get(pojo));" + "\n")
                .addAnnotation(AnnotationSpec.builder(ClassName.get(Test.class))
                        .build());
        return builder.build();
    }


    /**
     * Create a test for getter method in a class
     *
     * @param className    Class name to be tested
     * @param propertyName property name that has a getter to test
     * @param valueByType  value to test the getter
     * @param type
     * @return MethodSpec with a test for the getter
     */
    public static MethodSpec obtainGetter(String className, String propertyName, Object valueByType, String type) {
        String capitalizedPropertyName = StringUtils.capitalize(propertyName);
        String capitalizedType = StringUtils.capitalize(type);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("testGetter" + capitalizedPropertyName)
                .addModifiers(Modifier.PUBLIC)
                .addException(NoSuchFieldException.class)
                .addException(IllegalAccessException.class)
                .addCode("\tfinal " + className + " pojo = new " + className + "();\n\t"
                        + "final Field field = pojo.getClass().getDeclaredField(\"" + propertyName + "\");\n"
                        + "field.setAccessible(true);\n\n"
                        + "field.set(pojo," + valueByType + ");\n\n" + "assertEquals(FIELDS_WASNT_RETRIEVED_PROPERLY, " + valueByType + ", (" + capitalizedType + ") pojo.get" + capitalizedPropertyName + "());" + "\n")
                .addAnnotation(AnnotationSpec.builder(ClassName.get(Test.class))
                        .build());
        return builder.build();
    }

    public static MethodSpec obtainEquals(String className) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("equalsContract")
                .addModifiers(Modifier.PUBLIC)
                .addCode("\tEqualsVerifier.forClass(" + className + ".class).verify();\n")
                .addAnnotation(AnnotationSpec.builder(ClassName.get(Test.class))
                        .build());

        return builder.build();
    }
}
