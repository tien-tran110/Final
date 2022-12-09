
public class Token{
    String lexeme;
    int tokenCode;

    //constructor
    Token(String lexeme, int tokenCode){
        this.lexeme = lexeme;
        this.tokenCode = tokenCode;
    }

    Token(int tokenCode){
        this.tokenCode = tokenCode;
    }

}
