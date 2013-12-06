	
// ORIGINAL 


grammar Query;

options {
    output=AST;
    ASTLabelType=CommonTree; // type of $stat.tree ref etc...
}


@parser::members {
  @Override
  public void reportError(RecognitionException e) {
    throw new RuntimeException("LEXER FAIL"); 
  }
}

@lexer::members {
  @Override
  public void reportError(RecognitionException e) {
   throw new RuntimeException("PARSER FAIL"); 
  }
}



eval	 : expr EOF;
expr     : term terms;
terms    : PLUS expr|;
term     : factor factors;
factors  : TIME factor factors|factor factors|;
factor   : ID| LPAREN expr RPAREN;

PLUS     : '+';
//ID       : ('A'..'Z')+ ;
ID 		 : ('a'..'z'|'A'..'Z'|'0'..'9')+;
TIME     : '*';
LPAREN	 : '(';
RPAREN	 : ')';


WS     :   (' ' | '\t' | '\r'| '\n') {$channel=HIDDEN;};
