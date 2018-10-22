import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Man in the Middle code smell detector
 */
public class ManInTheMiddle extends VoidVisitorAdapter<Void> {

    private List<ClassOrInterfaceDeclaration> menInTheMiddle;
    private List<VariableDeclarator> classVars;

    public ManInTheMiddle(){
        menInTheMiddle = new ArrayList<>();
        classVars = new ArrayList<>();
    }

    public List<ClassOrInterfaceDeclaration> get(){
        return menInTheMiddle;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void args){
        if (!n.isPrivate()) {
            classVars.clear();
        }

        for (FieldDeclaration fieldDeclaration : n.getFields()){
            Type fieldType = fieldDeclaration.getElementType();
            if (fieldType.isClassOrInterfaceType()){
                classVars.addAll(fieldDeclaration.getVariables());
            }
        }

        int totalMethodCalls = n.findAll(MethodCallExpr.class).size();
        int methodCallsContaining = 0;

        for (VariableDeclarator var: classVars) {
            methodCallsContaining += (int) n.findAll(ReturnStmt.class).stream()
                    .filter(s -> s.findAll(MethodCallExpr.class).size() > 0)
                    .filter(s -> s.toString().contains(var.toString())).count();
        }

        if ((methodCallsContaining*1.0) / totalMethodCalls > 0.8 && classVars.size() < 2)
            menInTheMiddle.add(n);
        //visit inner classes as well
        super.visit(n, null);
    }
}
