package epfl.sweng.servercomm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * Asyncronous task that runs a background thread that sends a new
 * {@link Question} to the SwEng server.
 * 
 * Error types:
 * 		-1: Internal problem
 * 		 other code: Server HTTP response
 * 
 * @author Merok
 * @author born4new
 * 
 */
public class QuizEditExecution extends AsyncTask<JSONObject, Void, Integer> {
	
	private final static String SERVER_URL = "https://sweng-quiz.appspot.com";
	private HttpResponse response = null;

	@Override
	protected Integer doInBackground(JSONObject... jsonObject) {
		
		int responseStatus = -1;
		HttpPost post = new HttpPost(SERVER_URL + "/quizquestions/");
		
		// Send the quiz
		try {
			post.setEntity(new StringEntity(jsonObject[0].toString()));
			post.setHeader("Content-type", "application/json");
			response = SwengHttpClientFactory.getInstance().execute(post);
			responseStatus = response.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			// TODO: Error handling
			Log.e(this.getClass().getName(), "doInBackground(): StringEntity " +
					"couldn't be instantied.", e);
		} catch (ClientProtocolException e) {
			// TODO: Error handling
			Log.e(this.getClass().getName(), "doInBackground(): Error with " +
					"the HTTP protocol.", e);
		} catch (IOException e) {
			// TODO: Error handling
			Log.e(this.getClass().getName(), "doInBackground(): An I/O error " +
					"has occurred.", e);
		}

		return responseStatus;
	}
	
	/**
	 * Method executed right after the process is finished.
	 * @param result
	 */
	protected void onPostExecute(Long result) {
		TestCoordinator.check(TTChecks.NEW_QUESTION_SUBMITTED);
	}
}
