package epfl.sweng.test.servercomm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.backend.QuizQuery;
import epfl.sweng.servercomm.NetworkCommunication;
import junit.framework.TestCase;

public class ServerQueryTest extends TestCase{
	public void testQuerySuccesful(){
		NetworkCommunication mNetworkCommunication = new NetworkCommunication();
		QuizQuery query = new QuizQuery("(banana + garlic) fruit");
		JSONObject queryJSON;
		try {
			queryJSON = query.toJSON();
			assertEquals("(banana + garlic) fruit", queryJSON.get("query"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject result = mNetworkCommunication.retrieveQuizQuestions(query);
		try {
			JSONArray array = result.getJSONArray("questions");
			JSONObject question1 = array.getJSONObject(0);
			assertEquals("The fetching was not successful", 
					"How many calories are in a banana?"==question1.get("question"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
