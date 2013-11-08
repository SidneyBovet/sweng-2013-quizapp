package epfl.sweng.servercomm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;

import android.util.Log;
import epfl.sweng.quizquestions.QuizQuestion;

public class NetworkCommunication implements INetworkCommunication {

	private int mHttpStatusCommFailure = HttpStatus.SC_BAD_GATEWAY;
	
	@Override
	public int sendQuizQuestion(QuizQuestion question) {
		HttpPost postQuery = HttpFactory.getPostRequest(HttpFactory
				.getSwengBaseAddress() + "/quizquestions/");

		int responseStatus = -1;
		try {
			postQuery.setEntity(new StringEntity(question.toJSON().toString()));
			postQuery.setHeader("Content-type", "application/json");
			HttpResponse mResponse = SwengHttpClientFactory.getInstance()
					.execute(postQuery);
			responseStatus = mResponse.getStatusLine().getStatusCode();
			
			// TODO Explain what happened here (for now, we consider it voodoo).
			mResponse.getEntity().consumeContent();
		} catch (UnsupportedEncodingException e) {
			Log.e(this.getClass().getName(), "doInBackground(): Entity does "
					+ "not support the local encoding.", e);
			return mHttpStatusCommFailure;
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getName(), "doInBackground(): Error in the "
					+ "HTTP protocol.", e);
			return mHttpStatusCommFailure;
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "doInBackground(): An I/O error"
					+ "has occurred.", e);
			return mHttpStatusCommFailure;
		}
		return responseStatus;
	}

	@Override
	public QuizQuestion retrieveQuizQuestion() {
		
		QuizQuestion fetchedQuestion = null;
		
		String url = HttpFactory.getSwengFetchQuestion();
		HttpGet firstRandom = HttpFactory.getGetRequest(url);
		try {
			String jsonQuestion = SwengHttpClientFactory.getInstance()
					.execute(firstRandom, new BasicResponseHandler());
			fetchedQuestion = new QuizQuestion(jsonQuestion);
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getName(), "doInBackground(): Error in"
					+ "the HTTP protocol.", e);
			return null;
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "doInBackground(): An I/O"
					+ "error has occurred.", e);
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fetchedQuestion;
	}

}