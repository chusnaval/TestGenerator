package org.chusnaval;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

public class ClassVisitor  extends VoidVisitorAdapter<List<String>> {
    @Override
    public void visit(ClassOrInterfaceDeclaration n, List<String> arg) {
        super.visit(n, arg);
        for(FieldDeclaration fd : n.getFields()) {
            if(fd.isFinal() && fd.isStatic()) {
                arg.add(fd.toString());
            }
        }

    }
}


