import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer{
    private char ch;
    private String str;
    int position;
    Map<String, Integer> keywords = new HashMap<>();
    List<Token> tokens = new LinkedList<>();
    final String idRegex = "[_a-zA-Z0-9]{6,8}";
    final String boolRegex = "[(true)|(false)]";
    final String realRegex = "\\d+.\\d+";
    final String wholeRegex = "\\d+";
    //constructor
    Lexer(String source){
        keywords.put("batdau", 0);//start prpgram
        keywords.put("ketthuc", 1);//end pro
        keywords.put("laplai", 2);//while keywords
        keywords.put("neu", 3);//if
        keywords.put("khongthi", 4);//else
        keywords.put("t", 5);//datatype from -128 to 127
        keywords.put("i", 6);//-32768 to 32767
        keywords.put("e", 7);//-2,147,483,648 to 2,147,483,647
        keywords.put("n", 8);//-9,223,372,036,854,775,808 to 9,223,372,036,854,775,807	
        keywords.put("ko", 9);//boolean datatype
        this.str = source;
        this.position = 0;
        this.ch = str.charAt(position);
        
    }

    Token getToken(){
        //ignore white space
        while (Character.isWhitespace(this.ch)) {
            getNextChar();
        }

        switch (this.ch) {
            case '\u0000': return new Token("EOF", 99);
            case '+': getNextChar(); return new Token("+", 10);
            case '-': getNextChar(); return new Token("-", 11);
            case '*': getNextChar(); return new Token("*", 12);
            case '%': getNextChar(); return new Token("%", 13);
            case '^': getNextChar(); return new Token("%", 14);
            //case '/' int code = 15 but need further check for comment
            case '/': return div_or_cmt();
            case ';': getNextChar(); return new Token(";", 16);
            case ',': getNextChar(); return new Token(",", 17);
            case '{': getNextChar(); return new Token("{", 18);
            case '}': getNextChar(); return new Token("}", 19);
            case '(': getNextChar(); return new Token("(", 20);
            case ')': getNextChar(); return new Token(")", 21);
            case '#': getNextChar(); return new Token("#", 22);//parameter specifier
            case '&': getNextChar(); return new Token("&", 23);//logical and
            case '|': getNextChar(); return new Token("|", 24);//logical not

            case '<': return LT_or_LTE();// << : 25 ;  <== : 26; <>=: 27
            case '>': return GT_or_GTE();//<< : 28 ;  <== : 29
            case '=': return AS_or_EQ();// === : 30 ;  = : 31

            case '\'': return char_liter();//int 50
            case '"': return string_liter();//int 51
            
            default: return otherCases();
            //id : 60
            //real num : 61
            // natural num: 62
           
            
        } 
    }

    
    Token char_liter(){
        char c = getNextChar();//skip opening quote
        String lexemes = "";
        if(c == '\''){
            error("empty character constant");
        }
        else if( c == '\\'){
            c = getNextChar();
            if(c == 'n' || c == 't' || c == 'r' || c == '\\'){//possible escape character
                lexemes += c;
            }
            else{
                error(String.format("Unknown escapse sequence \\%c", c));
            }
        }

        if(getNextChar() != '\''){
            error("multi-character literal");
        }
        
        getNextChar();
        return new Token(lexemes, 50);
        
    }

    Token string_liter(){
        String output = "";
        while(getNextChar() != '\"'){
            if(this.ch == '\u0000'){//check for EOF
                error("EOF while scanning for string");

            }
            else if(this.ch == '\n'){
                error("EOL while scanning for string");
            }

            output += Character.toString(this.ch);
        } 
        
        getNextChar();
        return new Token(output, 51);
    }
   
    Token LT_or_LTE(){//<< or <== or <>=
      
        this.ch = getNextChar();
        if( this.ch == '<'){
            getNextChar();
            return new Token("<<", 25);
        }
        else if(this.ch == '>'){
            if(getNextChar() == '='){
                getNextChar();
                return new Token("<>=", 26);
            }

        }
        else if(this.ch == '='){
            getNextChar();
            if(this.ch == '='){
                getNextChar();
                return new Token("<==", 27);
            }
        }
        getNextChar();
        return getToken();
    }
    Token GT_or_GTE(){//>== or >>
        this.ch = getNextChar();
        if( this.ch == '>'){
            getNextChar();
            return new Token(">>", 28);
        }
        else if(this.ch == '='){
            this.ch = getNextChar();
            if(ch == '='){
                getNextChar();
                return new Token(">==", 29);
            }
        }
        getNextChar();
        return getToken();
    }
    Token AS_or_EQ(){//=== or =
        
        if(getNextChar() == '='){
            if(getNextChar() == '='){
                getNextChar();
                return new Token("===", 30);
            }
        }
        return new Token("=", 31);
    }

    Token otherCases(){

        if(Character.isAlphabetic(this.ch) || this.ch == '_'){
            return alphabetStart();
        }
        else if(Character.isDigit(this.ch)){
            return digitStart();
        }
        else{
            error("Unrecognized token");
        }
        getNextChar();
        return getToken();
    }

    //check for keywords or identifiers
    Token alphabetStart(){
        String lexeme = Character.toString(ch);
        getNextChar();
        
        while(this.ch != ' ' || this.ch != ',' || this.ch != '.' || this.ch != ';'
        || this.ch != '<' || this.ch != '>' || this.ch != '=' || this.ch != '~'
        || this.ch != '&' || this.ch != '|' || this.ch != '+' || this.ch != '-'
        || this.ch != '*' || this.ch != '/' || this.ch != '%' || this.ch != '^'){
            lexeme += Character.toString(this.ch);
        }

        if(!lexeme.equals("")){
            for (String output : keywords.keySet()) {
                if(output.equals(lexeme)){
                    getNextChar();
                    return new Token(lexeme, keywords.get(output));
                }
            }
            //in case it not keywords --> check indentifier
            Pattern pattern = Pattern.compile(idRegex);
            Matcher matcher = pattern.matcher(lexeme);
            boolean matchFound = matcher.find();
            if(matchFound) {
                getNextChar();
                return new Token(lexeme, 60);//id is code 60
                
            } else {
                getNextChar();
            }
        }
        return getToken();
        
    }

    //check for number
    Token digitStart(){
        String lexeme = Character.toString(this.ch);
        getNextChar();

        while(Character.isDigit(this.ch) || this.ch == '.'){
            lexeme += Character.toString(this.ch);
        }

        if(!lexeme.equals("")){
            //check for real number
            Pattern pattern1 = Pattern.compile(realRegex);
            Matcher matcher1 = pattern1.matcher(lexeme);
            //check for natural number
            Pattern pattern2 = Pattern.compile(wholeRegex);
            Matcher matcher2 = pattern2.matcher(lexeme);
            
            if(matcher1.find()) {
                getNextChar();
                return new Token(lexeme, 61);//real num code is 61
                
            } 
            else if(matcher2.find()){
                getNextChar();
                return new Token(lexeme, 62);//natural num code is 62
                
            }
            
        }
        getNextChar();
        return getToken();
    }

  
    Token div_or_cmt(){
        this.ch = getNextChar();
        //single line comment
        if(this.ch == '/'){
            while(getNextChar() != '\n'){
                getNextChar();
            }
            getNextChar();
            return getToken();
        }
        //multiline comment 
        else if(this.ch == '*'){
            while(getNextChar() != '*'){
                getNextChar();
            }
            if(this.ch == '*' && getNextChar() == '/'){
                getNextChar();
                return getToken();
            }
        }
        //after checking if not comment
        getNextChar();
        return new Token("/", 15);
    }

    //function to get next char
    char getNextChar(){
        this.position++;

        if(this.position >= this.str.length()){
            this.ch = '\u0000';
            return this.ch;
        }

        this.ch = this.str.charAt(this.position);
        return this.ch;
    }

    List<Token> tokenize(){
        Token t;
        while ((t = getToken()).tokenCode != 99) {
            tokens.add(t);
        }
        return tokens;
    }

    void error(String msg){
        System.out.println(msg);
        System.exit(-1); 
    }
}
