package epfl.sweng.backend;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Used to filter data from the SwEng server.
 * 
 * @author born4new
 * 
 */
public class QuizQuery {

	private String query;

	public QuizQuery(String query) {
		if (QuizQuery.isQueryValid(query)) {
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
	 * John is a wizard.
	 * 
	 * @param query
	 *            Query to be verified.
	 * @return true if valid, false otherwise.
	 */
	public static boolean isQueryValid(String query) {

		//
		// verify : no characters other than alphanumeric characters,
		// ' ',(,),*,+
		boolean expectedChara = hasExpectedCharacters(query);
		// verifiy that the syntax is correct: (i.e banana++* is not accepted)
		boolean correctSyntax = hasGoodSyntax(query);
		// verifiy that the nested parenthesis are correct: (i.e (banana)) is
		// not accepted)
		boolean correctNested = isWellNested(query);

		return true;
	}

	private static boolean hasGoodSyntax(String query) {

		return false;
	}

	private static boolean hasExpectedCharacters(String query) {
		String alphanumeric = "(?:[a-zA-Z0-9])+";
		String allowedOperators = "(?:\\+|\\*|\\s|\\(|\\))*";
		return query.matches(alphanumeric + allowedOperators);
	}

	private static boolean isWellNested(String query) {
		String onlyParenthesiString = query.replaceAll("[^\\(\\)]", "");

		if (query.length() % 2 != 0) {
			return false;
		}

		String[] array = onlyParenthesiString.split("");
		int nestedCounter = 0;
		for (String c : array) {
			nestedCounter += c.equals("(") ? 1 : -1;
			if (nestedCounter < 0) {
				return false;
			}
		}

		return true;
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
