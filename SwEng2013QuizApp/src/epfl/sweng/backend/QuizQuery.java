package epfl.sweng.backend;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.interpreter.QueryLexer;
import epfl.sweng.interpreter.QueryParser;

/**
 * Used to filter data from the SwEng server.
 * 
 * @author born4new
 * @author Merok
 * 
 */
public class QuizQuery {

	private String query;

	public QuizQuery(String query) {
		if (this.hasGoodSyntax(query)) {
			this.query = query;
		}
	}

	public QuizQuery(JSONObject jsonQuery) {
		String from = null;
		try {
			from = jsonQuery.getString("next");
			jsonQuery.put("from", from);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.query = jsonQuery.toString();
	}

	/**
	 * Verify that that the querry has a syntax that follow the correct Grammar
	 * (i.e ")(banana++ fruit)" is not accepted). See Query.g for the grammar.
	 * 
	 * @param query
	 *            to be verified.
	 * @return true if it has a good Syntax, false otherwise.
	 */
	public boolean hasGoodSyntax(String query) {
		boolean ok = true;
		ANTLRStringStream in = new ANTLRStringStream(query);
		QueryLexer lexer = new QueryLexer(in);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		QueryParser parser = new QueryParser(tokens);

		try {
			parser.eval();
		} catch (Exception e) {
			ok = false;
		}
		return ok;
	}

	/**
	 * Returns a {@link JSONObject} representing the current query.
	 * 
	 * @return A JSONObject of the question.
	 */
	public JSONObject toJSON() throws JSONException {
		JSONObject jsonQuery = new JSONObject();
		jsonQuery.put("query", query);
		return jsonQuery;
	}
}
