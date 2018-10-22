import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Long Parameter List code smell detector
 */
public class LongParameterList extends VoidVisitorAdapter<Void> {

    private List<MethodDeclaration> longParameterMethods;

    public LongParameterList(){
        longParameterMethods = new ArrayList<>();
    }

    public List<MethodDeclaration> get(){
        return longParameterMethods;
    }

    @Override
    public void visit(MethodDeclaration n, Void args){
        int numParams = n.getParameters().size();
        if (numParams > 5) {
            longParameterMethods.add(n);
        }
    }
}
