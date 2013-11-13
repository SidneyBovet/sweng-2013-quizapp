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
import epfl.sweng.patterns.ConnectivityState;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * This class will do the actual communication with the Sweng server(s).
 * @author born4new
 *
 */
public class NetworkCommunication implements INetworkCommunication {

	private int mHttpStatusCommFailure = HttpStatus.SC_BAD_GATEWAY;
	
	@Override
	public int sendQuizQuestion(QuizQuestion question) {
		HttpPost postQuery = HttpFactory.getPostRequest(HttpFactory
				.getSwengBaseAddress() + "/quizquestions/");

		int httpCodeResponse = -1;
		try {
			postQuery.setEntity(new StringEntity(question.toJSON().toString()));
			postQuery.setHeader("Content-type", "application/json");
			HttpResponse response = SwengHttpClientFactory.getInstance()
					.execute(postQuery);
			httpCodeResponse = response.getStatusLine().getStatusCode();
			
			switch (httpCodeResponse) {
				case HttpStatus.SC_CREATED:
					break;
				
				case HttpStatus.SC_INTERNAL_SERVER_ERROR:
					UserPreferences.getInstance()
						.setConnectivityState(ConnectivityState.OFFLINE);
					break;
				
				case HttpStatus.SC_SERVICE_UNAVAILABLE:
					// XXX Not sure about this one, we'll have to check it again
					UserPreferences.getInstance()
						.setConnectivityState(ConnectivityState.OFFLINE);
					break;
					
				default:
					break;
			}
			
			response.getEntity().consumeContent();
		} catch (UnsupportedEncodingException e) {
			Log.e(this.getClass().getName(), "doInBackground(): Entity does "
					+ "not support the local encoding.", e);
			return mHttpStatusCommFailure;
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getName(), "doInBackground(): Error in the "
					+ "HTTP protocol.", e);
			UserPreferences.getInstance()
					.setConnectivityState(ConnectivityState.OFFLINE);
			return mHttpStatusCommFailure;
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "doInBackground(): An I/O error"
					+ "has occurred.", e);
			UserPreferences.getInstance()
					.setConnectivityState(ConnectivityState.OFFLINE);
			return mHttpStatusCommFailure;
		}
		return httpCodeResponse;
	}

	@Override
	public QuizQuestion retrieveQuizQuestion() {
		
		QuizQuestion fetchedQuestion = null;
		
		String url = HttpFactory.getSwengFetchQuestion();
		HttpGet firstRandom = HttpFactory.getGetRequest(url);
		try {
			HttpResponse response = SwengHttpClientFactory.getInstance()
					.execute(firstRandom);
			int httpCodeResponse = response.getStatusLine().getStatusCode();
			
			switch (httpCodeResponse) {
				case HttpStatus.SC_OK:
					String jsonQuestion = new BasicResponseHandler()
						.handleResponse(response);
					fetchedQuestion = new QuizQuestion(jsonQuestion);
					break;
				
				case HttpStatus.SC_INTERNAL_SERVER_ERROR:
					UserPreferences.getInstance()
						.setConnectivityState(ConnectivityState.OFFLINE);
					break;
				
				case HttpStatus.SC_SERVICE_UNAVAILABLE:
					// XXX Not sure about this one, we'll have to check it again
					UserPreferences.getInstance()
						.setConnectivityState(ConnectivityState.OFFLINE);
					break;
					
				default:
					break;
			}
			
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getName(), "doInBackground(): Error in"
					+ "the HTTP protocol.", e);
			UserPreferences.getInstance()
				.setConnectivityState(ConnectivityState.OFFLINE);
			return null;
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "doInBackground(): An I/O"
					+ "error has occurred.", e);
			UserPreferences.getInstance()
				.setConnectivityState(ConnectivityState.OFFLINE);
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fetchedQuestion;
	}

}
