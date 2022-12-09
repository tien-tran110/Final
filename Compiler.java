import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Compiler{
    public static void main(String[] args) throws FileNotFoundException{
        List<Token> output = new LinkedList<>();
        File f = new File("input.txt");
        Scanner s = new Scanner(f);
        String source = "";
        while (s.hasNext()) {
            source += s.nextLine() + "\n";
        }
        s.close();
        Lexer lexer = new Lexer(source);
        output = lexer.tokenize();
        //System.out.print(output);
        Parser syntax = new Parser(output);
        syntax.parse();
        
    }
}