package epfl.sweng.servercomm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONObject;

import android.os.AsyncTask;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/**
 * Asyncronous task that runs a background thread that sends a new {@link 
 * Question} to the SwEng server.
 * 
 * @author Merok
 * 
 */
public class QuizEditExecution extends AsyncTask<JSONObject, Void, Void> {
	private final static String SERVER_URL = "https://sweng-quiz.appspot.com";

	@Override
	protected Void doInBackground(JSONObject... jsonObject) {
		HttpPost post = new HttpPost(SERVER_URL + "/quizquestions/");
		// send the quiz
		ResponseHandler<String> handler = new BasicResponseHandler();
		try {
			post.setEntity(new StringEntity(jsonObject[0].toString()));
			post.setHeader("Content-type", "application/json");
			SwengHttpClientFactory.getInstance().execute(post, handler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TestCoordinator.check(TTChecks.NEW_QUESTION_SUBMITTED);
		return null;
	}
}
