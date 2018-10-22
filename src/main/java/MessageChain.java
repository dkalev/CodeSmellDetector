import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Message Chain code smell detector
 */
public class MessageChain extends VoidVisitorAdapter<Void> {

    private List<ClassOrInterfaceDeclaration> messageChains;
    private ClassOrInterfaceDeclaration currentClass;

    public MessageChain(){
        messageChains = new ArrayList<>();
    }

    public List<ClassOrInterfaceDeclaration> getMessageChains(){
        return messageChains;
    }


    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void args){
        if (!n.isPrivate())
            currentClass = n.asClassOrInterfaceDeclaration();
        super.visit(n, null);

    }

    @Override
    public void visit(MethodCallExpr n, Void args) {
        boolean parent = false;
        boolean child = false;
        try {
            Optional<Node> parentNode = n.getParentNode();
            if (parentNode.isPresent() && parentNode.get() instanceof MethodCallExpr) {
                boolean isOfTypeVoid = (((MethodCallExpr) parentNode.get()).calculateResolvedType().isVoid());
                boolean hasSameName = (((MethodCallExpr) parentNode.get()).getName().equals(n.getName()));

                if (parentNode.get() instanceof MethodCallExpr && !isOfTypeVoid && !hasSameName)
                    parent = true;

            }
            for (Node node : n.getChildNodes()) {
                if (node instanceof MethodCallExpr) {
                    boolean isOfTypeVoid = (((MethodCallExpr) node).calculateResolvedType().isVoid());
                    boolean hasSameName = (((MethodCallExpr) node).getName().equals(n.getName()));

                    if (!isOfTypeVoid && !hasSameName) {
                        child = true;
                        node.accept(this, null);
                    }
                }
            }
            if (parent && child) {
                if (!messageChains.contains(currentClass))
                    messageChains.add(currentClass);
            }
            //for some reason symbolsolver cannot resolve printf
        }catch (RuntimeException e){}

    }

}
