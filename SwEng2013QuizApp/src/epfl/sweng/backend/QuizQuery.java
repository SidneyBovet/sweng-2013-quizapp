package epfl.sweng.backend;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Used to filter data from the SwEng server.
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
	
	/**
	 * John is a wizard.
	 * @param query Query to be verified.
	 * @return true if valid, false otherwise.
	 */
	public static boolean isQueryValid(String query) {
		
		// John's magic
		
		return false;
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
