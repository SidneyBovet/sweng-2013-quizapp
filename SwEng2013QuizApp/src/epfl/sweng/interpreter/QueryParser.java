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
	public static final String[] tokenNames = new String[] { "<invalid>",
			"<EOR>", "<DOWN>", "<UP>", "ID", "LPAREN", "PLUS", "RPAREN",
			"TIME", "WS" };
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

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}

	@Override
	public String[] getTokenNames() {
		return QueryParser.tokenNames;
	}

	@Override
	public String getGrammarFileName() {
		return "Query.g";
	}

	@Override
	public void reportError(RecognitionException e) {
		throw new RuntimeException("LEXER FAIL");
	}

	public static class eval_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "eval"
	// Query.g:29:1: eval : expr EOF ;
	public final QueryParser.eval_return eval() throws RecognitionException {
		QueryParser.eval_return retval = new QueryParser.eval_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token EOF2 = null;
		ParserRuleReturnScope expr1 = null;

		CommonTree EOF2_tree = null;

		try {
			// Query.g:29:7: ( expr EOF )
			// Query.g:29:9: expr EOF
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_expr_in_eval61);
				expr1 = expr();
				state._fsp--;

				adaptor.addChild(root_0, expr1.getTree());

				EOF2 = (Token) match(input, EOF, FOLLOW_EOF_in_eval63);
				EOF2_tree = (CommonTree) adaptor.create(EOF2);
				adaptor.addChild(root_0, EOF2_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "eval"

	public static class expr_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "expr"
	// Query.g:30:1: expr : term terms ;
	public final QueryParser.expr_return expr() throws RecognitionException {
		QueryParser.expr_return retval = new QueryParser.expr_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope term3 = null;
		ParserRuleReturnScope terms4 = null;

		try {
			// Query.g:30:10: ( term terms )
			// Query.g:30:12: term terms
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_term_in_expr74);
				term3 = term();
				state._fsp--;

				adaptor.addChild(root_0, term3.getTree());

				pushFollow(FOLLOW_terms_in_expr76);
				terms4 = terms();
				state._fsp--;

				adaptor.addChild(root_0, terms4.getTree());

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "expr"

	public static class terms_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "terms"
	// Query.g:31:1: terms : ( PLUS expr |);
	public final QueryParser.terms_return terms() throws RecognitionException {
		QueryParser.terms_return retval = new QueryParser.terms_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token PLUS5 = null;
		ParserRuleReturnScope expr6 = null;

		CommonTree PLUS5_tree = null;

		try {
			// Query.g:31:10: ( PLUS expr |)
			int alt1 = 2;
			int LA1_0 = input.LA(1);
			if ((LA1_0 == PLUS)) {
				alt1 = 1;
			} else if ((LA1_0 == EOF || LA1_0 == RPAREN)) {
				alt1 = 2;
			}

			else {
				NoViableAltException nvae = new NoViableAltException("", 1, 0,
						input);
				throw nvae;
			}

			switch (alt1) {
			case 1:
			// Query.g:31:12: PLUS expr
			{
				root_0 = (CommonTree) adaptor.nil();

				PLUS5 = (Token) match(input, PLUS, FOLLOW_PLUS_in_terms86);
				PLUS5_tree = (CommonTree) adaptor.create(PLUS5);
				adaptor.addChild(root_0, PLUS5_tree);

				pushFollow(FOLLOW_expr_in_terms88);
				expr6 = expr();
				state._fsp--;

				adaptor.addChild(root_0, expr6.getTree());

			}
				break;
			case 2:
			// Query.g:31:22:
			{
				root_0 = (CommonTree) adaptor.nil();

			}
				break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "terms"

	public static class term_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "term"
	// Query.g:32:1: term : factor factors ;
	public final QueryParser.term_return term() throws RecognitionException {
		QueryParser.term_return retval = new QueryParser.term_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope factor7 = null;
		ParserRuleReturnScope factors8 = null;

		try {
			// Query.g:32:10: ( factor factors )
			// Query.g:32:12: factor factors
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_factor_in_term100);
				factor7 = factor();
				state._fsp--;

				adaptor.addChild(root_0, factor7.getTree());

				pushFollow(FOLLOW_factors_in_term102);
				factors8 = factors();
				state._fsp--;

				adaptor.addChild(root_0, factors8.getTree());

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "term"

	public static class factors_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "factors"
	// Query.g:33:1: factors : ( TIME factor factors | factor factors |);
	public final QueryParser.factors_return factors()
			throws RecognitionException {
		QueryParser.factors_return retval = new QueryParser.factors_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TIME9 = null;
		ParserRuleReturnScope factor10 = null;
		ParserRuleReturnScope factors11 = null;
		ParserRuleReturnScope factor12 = null;
		ParserRuleReturnScope factors13 = null;

		CommonTree TIME9_tree = null;

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
				root_0 = (CommonTree) adaptor.nil();

				TIME9 = (Token) match(input, TIME, FOLLOW_TIME_in_factors110);
				TIME9_tree = (CommonTree) adaptor.create(TIME9);
				adaptor.addChild(root_0, TIME9_tree);

				pushFollow(FOLLOW_factor_in_factors112);
				factor10 = factor();
				state._fsp--;

				adaptor.addChild(root_0, factor10.getTree());

				pushFollow(FOLLOW_factors_in_factors114);
				factors11 = factors();
				state._fsp--;

				adaptor.addChild(root_0, factors11.getTree());

			}
				break;
			case 2:
			// Query.g:33:32: factor factors
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_factor_in_factors116);
				factor12 = factor();
				state._fsp--;

				adaptor.addChild(root_0, factor12.getTree());

				pushFollow(FOLLOW_factors_in_factors118);
				factors13 = factors();
				state._fsp--;

				adaptor.addChild(root_0, factors13.getTree());

			}
				break;
			case 3:
			// Query.g:33:47:
			{
				root_0 = (CommonTree) adaptor.nil();

			}
				break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "factors"

	public static class factor_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "factor"
	// Query.g:34:1: factor : ( ID | LPAREN expr RPAREN );
	public final QueryParser.factor_return factor() throws RecognitionException {
		QueryParser.factor_return retval = new QueryParser.factor_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token ID14 = null;
		Token LPAREN15 = null;
		Token RPAREN17 = null;
		ParserRuleReturnScope expr16 = null;

		CommonTree ID14_tree = null;
		CommonTree LPAREN15_tree = null;
		CommonTree RPAREN17_tree = null;

		try {
			// Query.g:34:10: ( ID | LPAREN expr RPAREN )
			int alt3 = 2;
			int LA3_0 = input.LA(1);
			if ((LA3_0 == ID)) {
				alt3 = 1;
			} else if ((LA3_0 == LPAREN)) {
				alt3 = 2;
			}

			else {
				NoViableAltException nvae = new NoViableAltException("", 3, 0,
						input);
				throw nvae;
			}

			switch (alt3) {
			case 1:
			// Query.g:34:12: ID
			{
				root_0 = (CommonTree) adaptor.nil();

				ID14 = (Token) match(input, ID, FOLLOW_ID_in_factor128);
				ID14_tree = (CommonTree) adaptor.create(ID14);
				adaptor.addChild(root_0, ID14_tree);

			}
				break;
			case 2:
			// Query.g:34:16: LPAREN expr RPAREN
			{
				root_0 = (CommonTree) adaptor.nil();

				LPAREN15 = (Token) match(input, LPAREN,
						FOLLOW_LPAREN_in_factor131);
				LPAREN15_tree = (CommonTree) adaptor.create(LPAREN15);
				adaptor.addChild(root_0, LPAREN15_tree);

				pushFollow(FOLLOW_expr_in_factor133);
				expr16 = expr();
				state._fsp--;

				adaptor.addChild(root_0, expr16.getTree());

				RPAREN17 = (Token) match(input, RPAREN,
						FOLLOW_RPAREN_in_factor135);
				RPAREN17_tree = (CommonTree) adaptor.create(RPAREN17);
				adaptor.addChild(root_0, RPAREN17_tree);

			}
				break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "factor"

	// Delegated rules

	public static final BitSet FOLLOW_expr_in_eval61 = new BitSet(
			new long[] { 0x0000000000000000L });
	public static final BitSet FOLLOW_EOF_in_eval63 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_term_in_expr74 = new BitSet(
			new long[] { 0x0000000000000040L });
	public static final BitSet FOLLOW_terms_in_expr76 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_PLUS_in_terms86 = new BitSet(
			new long[] { 0x0000000000000030L });
	public static final BitSet FOLLOW_expr_in_terms88 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_factor_in_term100 = new BitSet(
			new long[] { 0x0000000000000130L });
	public static final BitSet FOLLOW_factors_in_term102 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_TIME_in_factors110 = new BitSet(
			new long[] { 0x0000000000000030L });
	public static final BitSet FOLLOW_factor_in_factors112 = new BitSet(
			new long[] { 0x0000000000000130L });
	public static final BitSet FOLLOW_factors_in_factors114 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_factor_in_factors116 = new BitSet(
			new long[] { 0x0000000000000130L });
	public static final BitSet FOLLOW_factors_in_factors118 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_ID_in_factor128 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_LPAREN_in_factor131 = new BitSet(
			new long[] { 0x0000000000000030L });
	public static final BitSet FOLLOW_expr_in_factor133 = new BitSet(
			new long[] { 0x0000000000000080L });
	public static final BitSet FOLLOW_RPAREN_in_factor135 = new BitSet(
			new long[] { 0x0000000000000002L });
}
