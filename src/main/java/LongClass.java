import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Long Class code smell detector
 */
public class LongClass extends GenericVisitorAdapter<Integer, Void> {

    private LongMethod lm;
    private List<ClassOrInterfaceDeclaration> longClasses;

    public LongClass(){
        lm = new LongMethod();
        longClasses = new ArrayList<>();
    }

    public List<ClassOrInterfaceDeclaration> getLongClasses(){
        return longClasses;
    }

    @Override
    public Integer visit(ClassOrInterfaceDeclaration n, Void args){
//        System.out.println("Class Name: " + n.getName());
        int length = 1;
        for (Node child : n.getMembers()){
            if (child instanceof FieldDeclaration) {
                length += 1;
            }
            else if (child instanceof ConstructorDeclaration){
                length += child.accept(this, null);
            }
            else if (child instanceof MethodDeclaration){
                length += child.accept(this, null);
            }
        }
        if (length >= 100) {
            longClasses.add(n);
//            System.out.println(n.getName() + " is a long class with size of " + length);
        }
        return length;
    }

    @Override
    public Integer visit(ConstructorDeclaration n, Void args){
        int length = 1;
        for (Node node : n.getBody().getChildNodes()){
            //should be able to evaluate node instance of Statement to true, but doesn't
            if (node instanceof ExpressionStmt){
                length += 1;
            }
        }
        return length;
    }

    @Override
    public Integer visit(MethodDeclaration n, Void args){
        return lm.visitMethod(n, null) + 1;
    }
}
