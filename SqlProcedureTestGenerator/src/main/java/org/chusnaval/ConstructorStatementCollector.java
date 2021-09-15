package org.chusnaval;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

public class ConstructorStatementCollector extends VoidVisitorAdapter<List<Statement>> {

    @Override
    public void visit(ConstructorDeclaration c, List<Statement> list) {
        super.visit(c, list);
        list.addAll(c.getBody().getStatements());
    }

}
