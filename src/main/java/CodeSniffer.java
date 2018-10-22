import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main driver of the application
 * loads the files to be investigated
 * sets up the javaparser and symbolsolver environments
 */
public class CodeSniffer {
    private static final String DIR_PATH = "src//main//resources";

    public static void main(String[] args) throws IOException {
        System.out.println("I think I can smell something...");

        initSymbolSolver();

        Path sourceDir = Paths.get(DIR_PATH);
        SourceRoot sourceRoot = new SourceRoot(sourceDir);
        sourceRoot.setParserConfiguration(JavaParser.getStaticConfiguration());
        List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse("");

        List<CompilationUnit> allCus = parseResults.stream()
                .filter(ParseResult::isSuccessful)
                .map(r -> r.getResult().get())
                .collect(Collectors.toList());


        //initialize the code smell detectors
        LongMethod longMethod = new LongMethod();
        LongParameterList longParameterList = new LongParameterList();
        LongClass longClass = new LongClass();
        DataClass dataClass = new DataClass();
        MessageChain messageChain = new MessageChain();
        ManInTheMiddle manInTheMiddle = new ManInTheMiddle();

        allCus.forEach(c -> {
            c.accept(longMethod, null);
            c.accept(longParameterList, null);
            c.accept(longClass, null);
            c.accept(dataClass, null);
            c.accept(messageChain, null);
            c.accept(manInTheMiddle, null);
        });

        System.out.println("Long methods");
        System.out.println("-------------");
        for (MethodDeclaration md : longMethod.getLongMethods()){
            System.out.println(md.getName());
        }
        System.out.println("-------------");
        System.out.println("Methods with long parameter lists");
        System.out.println("-------------");
        for (MethodDeclaration md : longParameterList.get()){
            System.out.println(md.getName());
        }
        System.out.println("-------------");
        System.out.println("Long classes");
        System.out.println("-------------");
        for (ClassOrInterfaceDeclaration c : longClass.getLongClasses()){
            System.out.println(c.getName());
        }
        System.out.println("-------------");
        System.out.println("Data classes");
        System.out.println("-------------");
        for (ClassOrInterfaceDeclaration c : dataClass.get()){
            System.out.println(c.getName());
        }
        System.out.println("-------------");
        System.out.println("Message chains");
        System.out.println("-------------");
        for (ClassOrInterfaceDeclaration c : messageChain.getMessageChains()){
            System.out.println(c.getName());
        }
        System.out.println("-------------");
        System.out.println("Man in the middle");
        System.out.println("-------------");
        for (ClassOrInterfaceDeclaration c : manInTheMiddle.get()){
            System.out.println(c.getName());
        }


    }

    private static void initSymbolSolver(){
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver()); //for libraries part of the language
        typeSolver.add(new JavaParserTypeSolver(new File(DIR_PATH))); // for all the files in the source dir
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);
    }

}
