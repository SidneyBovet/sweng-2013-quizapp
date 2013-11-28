package epfl.sweng.test.servercomm;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.servercomm.NetworkCommunication;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;

public class ServerQueryTest extends TestCase {
	private MockHttpClient mMockClient;

	@Override
	public void setUp() {
		mMockClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(mMockClient);
	}

	@Override
	public void tearDown() {

	}
	
	public void testQuerySuccesful() throws JSONException {
		mMockClient.pushCannedResponse(
				"POST (?:https?://[^/]+|[^/]+)?/+search\\b",
                HttpStatus.SC_OK,
                "{\"questions\": [ {\"question\": \"q1\","
                + " \"answers\": [\"a1\", \"b1\"], \"owner\": \"o1\","
                + " \"solutionIndex\": 0, \"tags\": [\"fruit\", \"t1\"], \"id\": \"1\" }, " +
                "{\"question\": \"q2\","
                + " \"answers\": [\"a2\", \"b2\", \"c2\"], \"owner\": \"o2\","
                + " \"solutionIndex\": 0, \"tags\": [\"fruit\"], \"id\": \"2\" }]," +
                "\"next\": null }",
                "application/json");
		
		NetworkCommunication mNetworkCommunication = new NetworkCommunication();
		QuizQuery query = new QuizQuery("fruit", "no fromm string... whaaaat?");
		JSONObject queryJSON;
		try {
			queryJSON = query.toJSON();
			assertEquals("fruit", queryJSON.get("query"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		JSONObject result = mNetworkCommunication.retrieveQuizQuestions(query);
		Log.v("JSON string", result.toString(0));
		try {
			JSONArray array = result.getJSONArray("questions");
			JSONObject question1 = array.getJSONObject(0);
			assertEquals("q1", question1.get("question"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
