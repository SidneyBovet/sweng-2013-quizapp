package epfl.sweng.backend;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.util.Log;

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
			Log.e(mQuizQuery.getClass().getName(), "toJSON(): "
					+ "QuizQuery JSON input was incorrect.", e);
			fail("Exception when hard creating JSONobject");
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
			Log.e(mQuizQuery.getClass().getName(), "toJSON(): "
					+ "QuizQuery JSON input was incorrect.", e);
			fail("Exception when hard creating JSONobject");
		}
		assertTrue("json objects must have same \"query\"",
				strJsonFromMethod.equals(strJsonQuery));
		assertTrue("json objects must have same \"from\"",
				fromJsonQuery.equals(fromJsonFromMethod));

	}
	
	public void testParcelable(){
	    // Obtain a Parcel object and write the parcelable object to it:
		mQuizQuery = new QuizQuery("adibou", "xxxxx");
	    Parcel parcel = Parcel.obtain();
	    mQuizQuery.writeToParcel(parcel, 0);

	    // After you're done with writing, you need to reset the parcel for reading:
	    parcel.setDataPosition(0);

	    // Reconstruct object from parcel and asserts:
	    QuizQuery createdFromParcel = QuizQuery.CREATOR.createFromParcel(parcel);
	    assertEquals(mQuizQuery.getQuery(), createdFromParcel.getQuery());
	    assertEquals(mQuizQuery.getFrom(), createdFromParcel.getFrom());
	}
}
