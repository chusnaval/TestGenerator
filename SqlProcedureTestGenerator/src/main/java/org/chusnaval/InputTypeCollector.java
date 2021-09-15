package org.chusnaval;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

public class InputTypeCollector extends VoidVisitorAdapter<List<ParameterType>> {

    @Override
    public void visit(MethodDeclaration c, List<ParameterType> list) {
        super.visit(c, list);

        if (c.getName().toString().equals("execute")) {
            for(Parameter parameter : c.getParameters()){
                list.add(new ParameterType(parameter.getNameAsString(), parameter.getTypeAsString()));
            }
        }
    }
}
