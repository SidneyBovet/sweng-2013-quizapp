package epfl.sweng.backend;

import org.json.JSONException;
import org.json.JSONObject;

import junit.framework.TestCase;

public class QuizQueryTest extends TestCase {
	private QuizQuery mQuizQuery;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	// Don't really need to test hasGoodSyntax() because it has already be done
	// in SearchActivityTest
	public void testHasGoodSyntax() {
		mQuizQuery = new QuizQuery(")()+==?=?=__:", "");
		assertFalse("must fail since query is bad", mQuizQuery.hasGoodSyntax());
	}

	public void testToJsonWithFromEqualsToNull() {
		mQuizQuery = new QuizQuery("adibou", null);
		JSONObject jsonQuery = new JSONObject();
		JSONObject jsonFromMethod = new JSONObject();
		String strJsonQuery = null;
		String strJsonFromMethod = null;
		Boolean boleanFromJsonQuery = null;
		Boolean booleanFromJsonFromMethod = null;
		try {
			jsonQuery.put("query", "adibou");
			jsonFromMethod = mQuizQuery.toJSON();
			strJsonQuery = jsonQuery.getString("query");
			strJsonFromMethod = jsonFromMethod.getString("query");
			boleanFromJsonQuery = jsonQuery.has("from");
			booleanFromJsonFromMethod = jsonFromMethod.has("from");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		assertTrue("json objects must have same \"query\"",
				strJsonFromMethod.equals(strJsonQuery));
		assertTrue("json objects must have null \"from\"",
				boleanFromJsonQuery == false
						&& booleanFromJsonFromMethod == false);

	}

	public void testToJsonWithFromNotEqualsToNull() {
		mQuizQuery = new QuizQuery("adibou", "xxxxx");
		JSONObject jsonQuery = new JSONObject();
		JSONObject jsonFromMethod = new JSONObject();
		String strJsonQuery = null;
		String strJsonFromMethod = null;
		String fromJsonQuery = null;
		String fromJsonFromMethod = null;
		try {
			jsonQuery.put("query", "adibou");
			jsonQuery.put("from", "xxxxx");
			jsonFromMethod = mQuizQuery.toJSON();
			strJsonQuery = jsonQuery.getString("query");
			strJsonFromMethod = jsonFromMethod.getString("query");
			fromJsonQuery = jsonQuery.getString("from");
			fromJsonFromMethod = jsonFromMethod.getString("from");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		assertTrue("json objects must have same \"query\"",
				strJsonFromMethod.equals(strJsonQuery));
		assertTrue("json objects must have same \"from\"",
				fromJsonQuery.equals(fromJsonFromMethod));

	}
}
