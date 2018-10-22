import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Data Clumps code smell detector
 */
public class DataClumps extends VoidVisitorAdapter<Void> {

    private static int SIZE_THRESHOLD = 3;
    private static int NUM_METHODS_THRESHOLD = 1;
    private HashSet<SimpleName> fieldDeclarationVars;
    private ArrayList<HashSet<SimpleName>> methodCallVars;

    public DataClumps(){
        fieldDeclarationVars = new HashSet<>();
        methodCallVars = new ArrayList<>();
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void args){
        for (FieldDeclaration fd : n.getFields()){
            for (VariableDeclarator var :fd.getVariables()){
                fieldDeclarationVars.add(var.getName());
            }
        }

        int numMethods = (int) n.getMethods().stream()
                .filter(m -> m.getParameters().size() >= SIZE_THRESHOLD)
                .count();
        super.visit(n, null);
        if (!fieldDeclarationVars.isEmpty() &&
                fieldDeclarationVars.size() >= SIZE_THRESHOLD &&
                numMethods >= NUM_METHODS_THRESHOLD){
            System.out.println("data clump in " + n.getName());
        }
    }

    @Override
    public void visit(MethodCallExpr n, Void args){
        for (Expression e : n.getArguments()){
            methodCallVars.add(getVariables(e));
            fieldDeclarationVars.retainAll(getVariables(e));
        }
    }

    private HashSet<SimpleName> getVariables(Node n){
        HashSet<SimpleName> vars = new HashSet<>();
        for (Node node : n.getChildNodes()){
            vars.addAll(node.findAll(SimpleName.class));
        }
        return vars;
    }
}
