import java.util.List;

public class Parser {

    private List<Token> source;
    private Token token;
	private int position;
    
    //constructor
    Parser(List<Token> source) {
		this.source = source;
        this.position = 0;
		this.token = source.get(position);

	}
    void parse(){
        if(this.token.tokenCode == 0){//begin of a program
            System.out.println("Enter program <stmt>");
            while(getNextToken().tokenCode != 1){//end of a program
                stmt();
            }
            if(this.token.tokenCode == 1){
                System.out.println("Parse successfull");
            }
            else{
                error("Expected ending: 'ketthuc'");
            }
        }
        else{
            error("Expected heading: 'batdau'");
        }
    }

    // <stmt> --> <ifstmt> | <while_loop> | <as_s> | <declaration>
    void stmt(){
        if(this.token.tokenCode == 3){//if code
            System.out.println("Enter <ifstmt>");
            ifstmt();
        }
        else if(this.token.tokenCode == 2){//while code
            System.out.println("Enter <while_loop>");
            while_loop();
        }
        else if(this.token.tokenCode == 60){//ident code
            System.out.println("Enter <assign>");
            assign();
        }
        else{
            System.out.println("Enter <declaration>");
            declaration();
        }
    }

    //ifstmt --> neu '(' <bool_exp> ')' <block> khongthi <block>
    void ifstmt(){
        if(getNextToken().tokenCode  == 20){//(
            bool_exp();
            if(getNextToken().tokenCode  == 21){//) code
                block();
                if(getNextToken().tokenCode  == 4){//else code
                    block();
                    System.out.println("Parse <ifstmt> done");
                }
                else{
                    error("Expected 'khongthi'");//else
                }
            }
            else{
                error("Expected ')'");
            }
        }
        else{
            error("Expected '('");
        } 
    }

    //while_loop --> laplai '(' <bool_exp> ')' <block>
    void while_loop(){
        
        if(getNextToken().tokenCode == 20){
            bool_exp();
            if(getNextToken().tokenCode == 21){
                block();
                System.out.println("Parse <while_loop> done");
            }
            else{
                error("Expected ')");
            }
        }
        else{
            error("Expected '(");
        }
    }

    //<block> --> '{' <stmt>; '}'
    void block(){
        if(getNextToken().tokenCode == 18){//{
            stmt();
            if(getNextToken().tokenCode == 16){//;
                if(getNextToken().tokenCode == 19){//}
                    System.out.println("Parse <block> done");
                }
                else{
                    error("Expected '}'");
                }
            }
            else{
                error("Expected ';'");
            }
        }
        else{
            error("Expected '{'");
        }

    }

    //<assign> --> ten '=' <expr>;
    void assign(){
        if(getNextToken().tokenCode == 30){//=
            System.out.println("Enter <expr>");
            expr();
            if(this.token.tokenCode == 16){//;
                System.out.println("Parse <assign> done");
            }
            else{
                error("Expected ';'");
            }
        }
        else{
            error("Expected '='");
        }
    }

    //<expr> --> <term> {(+ |-) <term>}
    void expr(){
        term();
        if(this.token.tokenCode == 10 || this.token.tokenCode == 11){
            term();
        }
        System.out.println("<expr> parse done");
    }

    //<term> --> <factor> {(*|/|%|^) <factor>} 
    void term(){
        factor();
        getNextToken();
        if(token.tokenCode == 12 || token.tokenCode == 15 || token.tokenCode == 13 || token.tokenCode == 14){ 
            factor();
        }
        System.out.println("<term> parse done");

    }

    //<factor> --> id | '(' <expr> ')' | <datatype>
    void factor(){
        getNextToken();
        if(token.tokenCode == 60){//id
            System.out.println("done <factor>");
        }
        else if(token.tokenCode == 20){//()
            expr();
            if(getNextToken().tokenCode == 21){//)
                System.out.println("done <factor>");
            }
            else{
                error("Expected ')");
            }
        }
        else{
            datatype();
            System.out.println("done <factor>");
        }
    }

    //<datatype> --> {t|i|e|n|ko}
    void datatype(){
       if(this.token.tokenCode == 5
       || this.token.tokenCode == 6
       || this.token.tokenCode == 7
       || this.token.tokenCode == 8
       || this.token.tokenCode == 9){//boolean datatype
        System.out.println("<datatype>");
        
       }
       else{
        error("Excpected datatype");
       }
        
    }

    //<declaration> --> <datatype> ten;
    void declaration(){
        if(getNextToken().tokenCode == 60){
            if(getNextToken().tokenCode == 16){
                System.out.println("Parse <declaration> done");
            }
            else{
                error("Expected ';'");
            }  
        }
        else{
            error("Expected identifier");//expected id
        }
    }

    //<bool_expr> -->  <expr> { << | >> | <== | >== | <>= | === | & | | |} <expr>  
    void bool_exp(){
        System.out.println("Enter <bool_exp>");
        expr();
        getNextToken();
        if(token.tokenCode == 25 || token.tokenCode == 26
        || token.tokenCode == 27 || token.tokenCode == 28
        || token.tokenCode == 29 || token.tokenCode == 30
        || token.tokenCode == 31 || token.tokenCode == 23
        || token.tokenCode == 24){
            expr();
            System.out.println("done <bool_exp>");
        }
        
    }
    void error(String msg){
        System.out.println(msg);
        System.exit(-1);
    }

    Token getNextToken() {
		this.token = this.source.get(this.position++);
		return this.token;
	}
}
