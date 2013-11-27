// $ANTLR 3.5.1 Query.g 2013-11-23 09:02:53
package epfl.sweng.interpreter;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

@SuppressWarnings("all")
public class QueryLexer extends Lexer {
	public static final int EOF = -1;
	public static final int ID = 4;
	public static final int LPAREN = 5;
	public static final int PLUS = 6;
	public static final int RPAREN = 7;
	public static final int TIME = 8;
	public static final int WS = 9;

	@Override
	public void reportError(RecognitionException e) {
		throw new RuntimeException("LEXER FAIL");
	}

	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public QueryLexer() {
	}

	public QueryLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public QueryLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override
	public String getGrammarFileName() {
		return "Query.g";
	}

	// $ANTLR start "PLUS"
	public final void mPLUS() throws RecognitionException {
		try {
			int type = PLUS;
			int channel = DEFAULT_TOKEN_CHANNEL;
			// Query.g:36:10: ( '+' )
			// Query.g:36:12: '+'
			{
				match('+');
			}

			state.type = type;
			state.channel = channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "PLUS"

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int type = ID;
			int channel = DEFAULT_TOKEN_CHANNEL;
			// Query.g:38:7: ( ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )+ )
			// Query.g:38:9: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )+
			{
				// Query.g:38:9: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' )+
				int cnt1 = 0;
				loop1: while (true) {
					int alt1 = 2;
					int la1 = input.LA(1);
					if ((la1 >= '0' && la1 <= '9')
							|| (la1 >= 'A' && la1 <= 'Z')
							|| (la1 >= 'a' && la1 <= 'z')) {
						alt1 = 1;
					}

					switch (alt1) {
						case 1:
						// Query.g:
						{
							if ((input.LA(1) >= '0' && input.LA(1) <= '9')
									|| (input.LA(1) >= 'A' && input.LA(1) <= 'Z')
									|| (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
								input.consume();
							} else {
								MismatchedSetException mse = new MismatchedSetException(
										null, input);
								recover(mse);
								throw mse;
							}
						}
							break;
	
						default:
							if (cnt1 >= 1) {
								break loop1;
							}
							EarlyExitException eee = new EarlyExitException(1,
									input);
							throw eee;
					}
					cnt1++;
				}
			}
			state.type = type;
			state.channel = channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "ID"

	// $ANTLR start "TIME"
	public final void mTIME() throws RecognitionException {
		try {
			int type = TIME;
			int channel = DEFAULT_TOKEN_CHANNEL;
			// Query.g:39:10: ( '*' )
			// Query.g:39:12: '*'
			{
				match('*');
			}

			state.type = type;
			state.channel = channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "TIME"

	// $ANTLR start "LPAREN"
	public final void mLPAREN() throws RecognitionException {
		try {
			int type = LPAREN;
			int channel = DEFAULT_TOKEN_CHANNEL;
			// Query.g:40:9: ( '(' )
			// Query.g:40:11: '('
			{
				match('(');
			}

			state.type = type;
			state.channel = channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "LPAREN"

	// $ANTLR start "RPAREN"
	public final void mRPAREN() throws RecognitionException {
		try {
			int type = RPAREN;
			int channel = DEFAULT_TOKEN_CHANNEL;
			// Query.g:41:9: ( ')' )
			// Query.g:41:11: ')'
			{
				match(')');
			}

			state.type = type;
			state.channel = channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "RPAREN"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int type = WS;
			int channel = DEFAULT_TOKEN_CHANNEL;
			// Query.g:44:8: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
			// Query.g:44:12: ( ' ' | '\\t' | '\\r' | '\\n' )
			{
				if ((input.LA(1) >= '\t' && input.LA(1) <= '\n')
						|| input.LA(1) == '\r' || input.LA(1) == ' ') {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(
							null, input);
					recover(mse);
					throw mse;
				}
				channel = HIDDEN;
			}

			state.type = type;
			state.channel = channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "WS"

	@Override
	public void mTokens() throws RecognitionException {
		// Query.g:1:8: ( PLUS | ID | TIME | LPAREN | RPAREN | WS )
		int alt2 = 6;
		switch (input.LA(1)) {
			case '+': {
				alt2 = 1;
			}
				break;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'G':
			case 'H':
			case 'I':
			case 'J':
			case 'K':
			case 'L':
			case 'M':
			case 'N':
			case 'O':
			case 'P':
			case 'Q':
			case 'R':
			case 'S':
			case 'T':
			case 'U':
			case 'V':
			case 'W':
			case 'X':
			case 'Y':
			case 'Z':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z': {
				alt2 = 2;
			}
				break;
			case '*': {
				alt2 = 3;
			}
				break;
			case '(': {
				alt2 = 4;
			}
				break;
			case ')': {
				alt2 = 5;
			}
				break;
			case '\t':
			case '\n':
			case '\r':
			case ' ': {
				alt2 = 6;
			}
				break;
			default:
				NoViableAltException nvae = new NoViableAltException("", 2, 0,
						input);
				throw nvae;
		}
		switch (alt2) {
			case 1:
			// Query.g:1:10: PLUS
			{
				mPLUS();
			}
				break;
			case 2:
			// Query.g:1:15: ID
			{
				mID();
			}
				break;
			case 3:
			// Query.g:1:18: TIME
			{
				mTIME();
			}
				break;
			case 4:
			// Query.g:1:23: LPAREN
			{
				mLPAREN();
			}
				break;
			case 5:
			// Query.g:1:30: RPAREN
			{
				mRPAREN();
			}
				break;
			case 6:
			// Query.g:1:37: WS
			{
				mWS();
			}
				break;
			default:
		}
	}
}
