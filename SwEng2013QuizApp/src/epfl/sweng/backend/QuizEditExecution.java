package epfl.sweng.backend;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONObject;

import android.os.AsyncTask;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;

/**
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
			TestingTransactions.check(TTChecks.NEW_QUESTION_SUBMITTED);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}