// $ANTLR 3.5.1 Query.g 2013-11-23 09:02:53
package epfl.sweng.interpreter;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;

@SuppressWarnings("all")
public class QueryParser extends Parser {
	public static final String[] TOKEN_NAMES = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "LPAREN", "PLUS", "RPAREN",
		"TIME", "WS"
	};
	public static final int EOF = -1;
	public static final int ID = 4;
	public static final int LPAREN = 5;
	public static final int PLUS = 6;
	public static final int RPAREN = 7;
	public static final int TIME = 8;
	public static final int WS = 9;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators

	public QueryParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}

	public QueryParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	private TreeAdaptor mAdaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.mAdaptor = adaptor;
	}

	public TreeAdaptor getTreeAdaptor() {
		return mAdaptor;
	}

	@Override
	public String[] getTokenNames() {
		return QueryParser.TOKEN_NAMES;
	}

	@Override
	public String getGrammarFileName() {
		return "Query.g";
	}

	@Override
	public void reportError(RecognitionException e) {
		throw new RuntimeException("LEXER FAIL");
	}

	public static class EvalReturn extends ParserRuleReturnScope {
		private CommonTree mTree;

		@Override
		public CommonTree getTree() {
			return mTree;
		}
	};

	// $ANTLR start "eval"
	// Query.g:29:1: eval : expr EOF ;
	public final QueryParser.EvalReturn eval() throws RecognitionException {
		QueryParser.EvalReturn retval = new QueryParser.EvalReturn();
		retval.start = input.LT(1);

		CommonTree root0 = null;

		Token eof2 = null;
		ParserRuleReturnScope expr1 = null;

		CommonTree eof2Tree = null;

		try {
			// Query.g:29:7: ( expr EOF )
			// Query.g:29:9: expr EOF
			{
				root0 = (CommonTree) mAdaptor.nil();

				pushFollow(FOLLOW_EXPR_IN_EVAL61);
				expr1 = expr();
				state._fsp--;

				mAdaptor.addChild(root0, expr1.getTree());

				eof2 = (Token) match(input, EOF, FOLLOW_EOF_IN_EVAL63);
				eof2Tree = (CommonTree) mAdaptor.create(eof2);
				mAdaptor.addChild(root0, eof2Tree);

			}

			retval.stop = input.LT(-1);

			retval.mTree = (CommonTree) mAdaptor.rulePostProcessing(root0);
			mAdaptor.setTokenBoundaries(retval.mTree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.mTree = (CommonTree) mAdaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "eval"

	public static class ExprReturn extends ParserRuleReturnScope {
		private CommonTree mTree;

		@Override
		public CommonTree getTree() {
			return mTree;
		}
	};

	// $ANTLR start "expr"
	// Query.g:30:1: expr : term terms ;
	public final QueryParser.ExprReturn expr() throws RecognitionException {
		QueryParser.ExprReturn retval = new QueryParser.ExprReturn();
		retval.start = input.LT(1);

		CommonTree root0 = null;

		ParserRuleReturnScope term3 = null;
		ParserRuleReturnScope terms4 = null;

		try {
			// Query.g:30:10: ( term terms )
			// Query.g:30:12: term terms
			{
				root0 = (CommonTree) mAdaptor.nil();

				pushFollow(FOLLOW_TERM_IN_EXPR74);
				term3 = term();
				state._fsp--;

				mAdaptor.addChild(root0, term3.getTree());

				pushFollow(FOLLOW_TERM_IN_EXPR76);
				terms4 = terms();
				state._fsp--;

				mAdaptor.addChild(root0, terms4.getTree());

			}

			retval.stop = input.LT(-1);

			retval.mTree = (CommonTree) mAdaptor.rulePostProcessing(root0);
			mAdaptor.setTokenBoundaries(retval.mTree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.mTree = (CommonTree) mAdaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "expr"

	public static class TermsReturn extends ParserRuleReturnScope {
		private CommonTree mTree;

		@Override
		public CommonTree getTree() {
			return mTree;
		}
	};

	// $ANTLR start "terms"
	// Query.g:31:1: terms : ( PLUS expr |);
	public final QueryParser.TermsReturn terms() throws RecognitionException {
		QueryParser.TermsReturn retval = new QueryParser.TermsReturn();
		retval.start = input.LT(1);

		CommonTree root0 = null;

		Token plus5 = null;
		ParserRuleReturnScope expr6 = null;

		CommonTree plus5tree = null;

		try {
			// Query.g:31:10: ( PLUS expr |)
			int alt1 = 2;
			int la1 = input.LA(1);
			if (la1 == PLUS) {
				alt1 = 1;
			} else if (la1 == EOF || la1 == RPAREN) {
				alt1 = 2;
			} else {
				NoViableAltException nvae = new NoViableAltException("", 1, 0,
						input);
				throw nvae;
			}

			switch (alt1) {
				case 1:
				// Query.g:31:12: PLUS expr
				{
					root0 = (CommonTree) mAdaptor.nil();
	
					plus5 = (Token) match(input, PLUS, FOLLOW_PLUS_IN_TERMS86);
					plus5tree = (CommonTree) mAdaptor.create(plus5);
					mAdaptor.addChild(root0, plus5tree);
	
					pushFollow(FOLLOW_EXPR_IN_TERMS88);
					expr6 = expr();
					state._fsp--;
	
					mAdaptor.addChild(root0, expr6.getTree());
	
				}
					break;
				case 2:
				// Query.g:31:22:
				{
					root0 = (CommonTree) mAdaptor.nil();
	
				}
					break;
				default:
					break;
			}
			retval.stop = input.LT(-1);

			retval.mTree = (CommonTree) mAdaptor.rulePostProcessing(root0);
			mAdaptor.setTokenBoundaries(retval.mTree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.mTree = (CommonTree) mAdaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "terms"

	public static class TermReturn extends ParserRuleReturnScope {
		private CommonTree mTree;

		@Override
		public CommonTree getTree() {
			return mTree;
		}
	};

	// $ANTLR start "term"
	// Query.g:32:1: term : factor factors ;
	public final QueryParser.TermReturn term() throws RecognitionException {
		QueryParser.TermReturn retval = new QueryParser.TermReturn();
		retval.start = input.LT(1);

		CommonTree root0 = null;

		ParserRuleReturnScope factor7 = null;
		ParserRuleReturnScope factors8 = null;

		try {
			// Query.g:32:10: ( factor factors )
			// Query.g:32:12: factor factors
			{
				root0 = (CommonTree) mAdaptor.nil();

				pushFollow(FOLLOW_FACTOR_IN_TERM100);
				factor7 = factor();
				state._fsp--;

				mAdaptor.addChild(root0, factor7.getTree());

				pushFollow(FOLLOW_FACTORS_IN_TERM102);
				factors8 = factors();
				state._fsp--;

				mAdaptor.addChild(root0, factors8.getTree());

			}

			retval.stop = input.LT(-1);

			retval.mTree = (CommonTree) mAdaptor.rulePostProcessing(root0);
			mAdaptor.setTokenBoundaries(retval.mTree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.mTree = (CommonTree) mAdaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "term"

	public static class FactorsReturn extends ParserRuleReturnScope {
		private CommonTree mTree;

		@Override
		public CommonTree getTree() {
			return mTree;
		}
	};

	// $ANTLR start "factors"
	// Query.g:33:1: factors : ( TIME factor factors | factor factors |);
	public final QueryParser.FactorsReturn factors() throws RecognitionException {
		QueryParser.FactorsReturn retval = new QueryParser.FactorsReturn();
		retval.start = input.LT(1);

		CommonTree root0 = null;

		Token time9 = null;
		ParserRuleReturnScope factor10 = null;
		ParserRuleReturnScope factors11 = null;
		ParserRuleReturnScope factor12 = null;
		ParserRuleReturnScope factors13 = null;

		CommonTree time9Tree = null;

		try {
			// Query.g:33:10: ( TIME factor factors | factor factors |)
			int alt2 = 3;
			switch (input.LA(1)) {
				case TIME: {
					alt2 = 1;
				}
					break;
				case ID:
				case LPAREN: {
					alt2 = 2;
				}
					break;
				case EOF:
				case PLUS:
				case RPAREN: {
					alt2 = 3;
				}
					break;
				default:
					NoViableAltException nvae = new NoViableAltException("", 2, 0,
							input);
					throw nvae;
			}
			switch (alt2) {
				case 1:
				// Query.g:33:12: TIME factor factors
				{
					root0 = (CommonTree) mAdaptor.nil();
	
					time9 = (Token) match(input, TIME, FOLLOW_TIME_IN_FACTORS110);
					time9Tree = (CommonTree) mAdaptor.create(time9);
					mAdaptor.addChild(root0, time9Tree);
	
					pushFollow(FOLLOW_FACTOR_IN_FACTORS112);
					factor10 = factor();
					state._fsp--;
	
					mAdaptor.addChild(root0, factor10.getTree());
	
					pushFollow(FOLLOW_FACTORS_IN_FACTORS114);
					factors11 = factors();
					state._fsp--;
	
					mAdaptor.addChild(root0, factors11.getTree());
	
				}
					break;
				case 2:
				// Query.g:33:32: factor factors
				{
					root0 = (CommonTree) mAdaptor.nil();
	
					pushFollow(FOLLOW_FACTOR_IN_FACTORS116);
					factor12 = factor();
					state._fsp--;
	
					mAdaptor.addChild(root0, factor12.getTree());
	
					pushFollow(FOLLOW_FACTORS_IN_FACTORS118);
					factors13 = factors();
					state._fsp--;
	
					mAdaptor.addChild(root0, factors13.getTree());
	
				}
					break;
				case 3:
				// Query.g:33:47:
				{
					root0 = (CommonTree) mAdaptor.nil();
	
				}
					break;
				default:
					break;

			}
			retval.stop = input.LT(-1);

			retval.mTree = (CommonTree) mAdaptor.rulePostProcessing(root0);
			mAdaptor.setTokenBoundaries(retval.mTree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.mTree = (CommonTree) mAdaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "factors"

	public static class FactorReturn extends ParserRuleReturnScope {
		private CommonTree mTree;

		@Override
		public CommonTree getTree() {
			return mTree;
		}
	};

	// $ANTLR start "factor"
	// Query.g:34:1: factor : ( ID | LPAREN expr RPAREN );
	public final QueryParser.FactorReturn factor() throws RecognitionException {
		QueryParser.FactorReturn retval = new QueryParser.FactorReturn();
		retval.start = input.LT(1);

		CommonTree root0 = null;

		Token id14 = null;
		Token lparen15 = null;
		Token rparen17 = null;
		ParserRuleReturnScope expr16 = null;

		CommonTree id14Tree = null;
		CommonTree lparen15Tree = null;
		CommonTree rparen17Tree = null;

		try {
			// Query.g:34:10: ( ID | LPAREN expr RPAREN )
			int alt3 = 2;
			int la3 = input.LA(1);
			if (la3 == ID) {
				alt3 = 1;
			} else if (la3 == LPAREN) {
				alt3 = 2;
			} else {
				NoViableAltException nvae = new NoViableAltException("", 3, 0,
						input);
				throw nvae;
			}

			switch (alt3) {
				case 1:
				// Query.g:34:12: ID
				{
					root0 = (CommonTree) mAdaptor.nil();
	
					id14 = (Token) match(input, ID, FOLLOW_ID_IN_FACTOR128);
					id14Tree = (CommonTree) mAdaptor.create(id14);
					mAdaptor.addChild(root0, id14Tree);
	
				}
					break;
				case 2:
				// Query.g:34:16: LPAREN expr RPAREN
				{
					root0 = (CommonTree) mAdaptor.nil();
	
					lparen15 = (Token) match(input, LPAREN,
							FOLLOW_LPAREN_IN_FACTOR131);
					lparen15Tree = (CommonTree) mAdaptor.create(lparen15);
					mAdaptor.addChild(root0, lparen15Tree);
	
					pushFollow(FOLLOW_EXPR_IN_FACTOR133);
					expr16 = expr();
					state._fsp--;
	
					mAdaptor.addChild(root0, expr16.getTree());
	
					rparen17 = (Token) match(input, RPAREN,
							FOLLOW_RPAREN_IN_FACTOR135);
					rparen17Tree = (CommonTree) mAdaptor.create(rparen17);
					mAdaptor.addChild(root0, rparen17Tree);
	
				}
					break;
				default:
					break;

			}
			retval.stop = input.LT(-1);

			retval.mTree = (CommonTree) mAdaptor.rulePostProcessing(root0);
			mAdaptor.setTokenBoundaries(retval.mTree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.mTree = (CommonTree) mAdaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "factor"

	// Delegated rules

	public static final BitSet FOLLOW_EXPR_IN_EVAL61 = new BitSet(
			new long[] {
				0x0000000000000000L
			});
	public static final BitSet FOLLOW_EOF_IN_EVAL63 = new BitSet(
			new long[] {
				0x0000000000000002L
			});
	public static final BitSet FOLLOW_TERM_IN_EXPR74 = new BitSet(
			new long[] {
				0x0000000000000040L
			});
	public static final BitSet FOLLOW_TERM_IN_EXPR76 = new BitSet(
			new long[] {
				0x0000000000000002L
			});
	public static final BitSet FOLLOW_PLUS_IN_TERMS86 = new BitSet(
			new long[] {
				0x0000000000000030L
			});
	public static final BitSet FOLLOW_EXPR_IN_TERMS88 = new BitSet(
			new long[] {
				0x0000000000000002L
			});
	public static final BitSet FOLLOW_FACTOR_IN_TERM100 = new BitSet(
			new long[] {
				0x0000000000000130L
			});
	public static final BitSet FOLLOW_FACTORS_IN_TERM102 = new BitSet(
			new long[] {
				0x0000000000000002L
			});
	public static final BitSet FOLLOW_TIME_IN_FACTORS110 = new BitSet(
			new long[] {
				0x0000000000000030L
			});
	public static final BitSet FOLLOW_FACTOR_IN_FACTORS112 = new BitSet(
			new long[] {
				0x0000000000000130L
			});
	public static final BitSet FOLLOW_FACTORS_IN_FACTORS114 = new BitSet(
			new long[] {
				0x0000000000000002L
			});
	public static final BitSet FOLLOW_FACTOR_IN_FACTORS116 = new BitSet(
			new long[] {
				0x0000000000000130L
			});
	public static final BitSet FOLLOW_FACTORS_IN_FACTORS118 = new BitSet(
			new long[] {
				0x0000000000000002L
			});
	public static final BitSet FOLLOW_ID_IN_FACTOR128 = new BitSet(
			new long[] {
				0x0000000000000002L
			});
	public static final BitSet FOLLOW_LPAREN_IN_FACTOR131 = new BitSet(
			new long[] {
				0x0000000000000030L
			});
	public static final BitSet FOLLOW_EXPR_IN_FACTOR133 = new BitSet(
			new long[] {
				0x0000000000000080L
			});
	public static final BitSet FOLLOW_RPAREN_IN_FACTOR135 = new BitSet(
			new long[] {
				0x0000000000000002L
			});
}
