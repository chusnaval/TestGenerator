package org.chusnaval;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;

public class TypeTestCalculator {

    /**
     * Default constructor
     */
    private TypeTestCalculator() {
        super();
    }

    /**
     * Obtain the type spec of a test class for a given entity
     *
     * @param entityClassName Entity class name to work with
     * @param methods         methods to test
     * @return the test class for the entity
     */
    public static TypeSpec getTypeSpec(String entityClassName, List<MethodSpec> methods) {
        String testClassName = entityClassName + "Test";
        return TypeSpec.classBuilder(testClassName).addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethods(methods)
                .addField(DataSource.class,"dataSource", Modifier.PRIVATE )
                .addField(Connection.class, "connection", Modifier.PRIVATE )
                .addField(CallableStatement.class, "callableStatement", Modifier.PRIVATE )
                .build();
    }
}
