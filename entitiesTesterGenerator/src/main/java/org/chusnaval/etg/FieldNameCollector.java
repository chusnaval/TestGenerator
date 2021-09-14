package org.chusnaval.etg;

import java.util.Map;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class FieldNameCollector extends VoidVisitorAdapter<Map<String, String>> {

    @Override
    public void visit(FieldDeclaration fd, Map<String, String> collector) {
        super.visit(fd, collector);
        for(VariableDeclarator vd: fd.getVariables()){
            collector.put(vd.getName().getId(), vd.getTypeAsString());
        }
    }

    
}
