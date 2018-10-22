import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Class code smell detector
 */
public class DataClass extends VoidVisitorAdapter<Void> {

    private boolean isDataClass;
    private List<ClassOrInterfaceDeclaration> dataClasses;

    public DataClass(){
        dataClasses = new ArrayList<>();
    }

    public List<ClassOrInterfaceDeclaration> get(){
        return dataClasses;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void args){
        isDataClass = true;
        //call super to visit nested classes
        super.visit(n, null);

        //if there are no fields => no data
        if (n.getMembers().stream().noneMatch(m -> m instanceof FieldDeclaration))
            isDataClass = false;
        n.getMethods().forEach(m -> m.accept(this, null));
        if (isDataClass)
            dataClasses.add(n);
    }

    @Override
    public void visit(MethodDeclaration n, Void args){
        if (!(isGetter(n) || isSetter(n) || isToString(n))){
            isDataClass = false;
        }
    }

    private Boolean isGetter(MethodDeclaration n){
        if (!n.isPublic())
            return false;
        if (n.getType().isVoidType())
            return false;
        if (!n.getName().asString().matches("^(get|is)[A-Z].*"))
            return false;
        return true;
    }

    private Boolean isSetter(MethodDeclaration n) {
        if (!n.isPublic())
            return false;
        if (!n.getType().isVoidType())
            return false;
        if (!n.getName().asString().matches("^set[A-Z].*"))
            return false;
        if (n.getParameters().size() != 1)
            return false;
        return true;
    }

    private Boolean isToString(MethodDeclaration n) {
        if (!n.getName().asString().matches("^toString$"))
            return false;
        if (!n.getType().toString().equals("String"))
            return false;
        return true;
    }
}
