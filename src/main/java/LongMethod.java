import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Long Method code smell detector
 */
public class LongMethod extends GenericVisitorAdapter<Integer, Void> {

    private List<MethodDeclaration> longMethods;

    public LongMethod() {
        longMethods = new ArrayList<>();
    }

    public List<MethodDeclaration> getLongMethods(){
        return longMethods;
    }

    @Override
    public Integer visit(ClassOrInterfaceDeclaration n, Void args){
        for (MethodDeclaration md : n.getMethods()){
            md.accept(this, null);
        }
        return 0;
    }

    @Override
    public Integer visit(MethodDeclaration n, Void args){
        int totalStmt = visitMethod(n, null);

        if (totalStmt > 10){
            longMethods.add(n);
        }
        //if you return a value the visitor stops and does not visit the rest of the methods
        return null;
    }

    @Override
    public Integer visit(IfStmt n, Void args){
        int total = 1;
        //add length of then block
        if (n.hasThenBlock()) {
            BlockStmt thenStmt = n.getThenStmt().asBlockStmt();
            total += visitStatement(thenStmt, null);
        }
        //add length of else block
        if (n.hasElseBlock()) {
            Statement elseBlock = n.getElseStmt().get();
            total += visitStatement(elseBlock, null);
        }
        //add length of else if (if present)
        Optional<Node> elseIf = n.getChildNodes().stream().filter(m -> m instanceof IfStmt).findFirst();
        if(elseIf.isPresent()){
            total += elseIf.get().accept(this, null);
        }
        return total;
    }

    @Override
    public Integer visit(ForStmt n, Void args){
        int total = 1;
        if (n.getBody().isBlockStmt())
            total += visitStatement(n.getBody().asBlockStmt(), null);
        return total;
    }

    @Override
    public Integer visit(WhileStmt n, Void args){
        int total = 1;
        if (n.getBody().isBlockStmt()){
            total += visitStatement(n.getBody().asBlockStmt(), null);
        }
        return total;
    }

    @Override
    public Integer visit(SwitchStmt n, Void args){
        return visitStatement(n, null);
    }

    @Override
    public Integer visit(DoStmt n, Void args) {
        int total = 1;
        if (n.getBody().isBlockStmt()){
            total += visitStatement(n.getBody().asBlockStmt(), null);
        }
        return total;
    }

    @Override
    public Integer visit(TryStmt n, Void args) {
        int total = 1;
        if (n.getTryBlock() != null){
            total += visitStatement(n.getTryBlock(), null);
        }
        for (CatchClause cc : n.getCatchClauses()) {
            if (cc.getBody() != null)
                total += visitStatement(cc.getBody(), null);
        }
        if (n.getFinallyBlock().isPresent())
            total += visitStatement(n.getFinallyBlock().get(), null);

        return total;
    }

    @Override
    public Integer visit(LambdaExpr n, Void args){
        //add 1 for the expression itself
        return visitStatement(n.getBody(), null) + 1;
    }

    public Integer visitMethod(MethodDeclaration n, Void args){
        if (!n.getBody().isPresent())
            return 0;
        BlockStmt blockStmt = n.getBody().get();

        return visitChildNodes(blockStmt.getChildNodes());
    }

    private Integer visitChildNodes(List<Node> children){
        int totalStmt = 0;
        for(Node node : children){
            if (node instanceof Statement){
                Integer childStmtCount = node.accept(this, null);
                if (childStmtCount != null){
                    totalStmt += childStmtCount;
                }else{
                    totalStmt++;
                }
            }else{
                totalStmt++;
            }
        }
        return totalStmt;
    }

    private Integer visitStatement(Statement n, Void args){
        return visitChildNodes(n.getChildNodes());
    }


}
