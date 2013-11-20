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
import org.json.JSONObject;

import android.util.Log;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.patterns.ConnectivityState;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * This class will do the actual communication with the Sweng server(s).
 * @author born4new
 *
 */
public class NetworkCommunication implements INetworkCommunication {

	private static final int BASE_SERVER_ERRORS = 5;
	private static final double ONE_HUNDRED = 100.0;
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
			
			if (Math.floor(httpCodeResponse/ONE_HUNDRED) == BASE_SERVER_ERRORS) {
				UserPreferences.getInstance()
					.setConnectivityState(ConnectivityState.OFFLINE);
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
	public QuizQuestion retrieveRandomQuizQuestion() {
		
		QuizQuestion fetchedQuestion = null;
		
		String url = HttpFactory.getSwengFetchQuestion();
		HttpGet firstRandom = HttpFactory.getGetRequest(url);
		try {
			HttpResponse response = SwengHttpClientFactory.getInstance()
					.execute(firstRandom);
			int httpCodeResponse = response.getStatusLine().getStatusCode();
			
			if (Math.floor(httpCodeResponse/ONE_HUNDRED) == BASE_SERVER_ERRORS) {
				UserPreferences.getInstance()
					.setConnectivityState(ConnectivityState.OFFLINE);
			} else if (httpCodeResponse == HttpStatus.SC_OK) {
				String jsonQuestion = new BasicResponseHandler()
					.handleResponse(response);
				fetchedQuestion = new QuizQuestion(jsonQuestion);
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

	@Override
	public JSONObject retrieveQuizQuestions(QuizQuery query) {
		HttpPost postQuery = HttpFactory.getPostRequest(HttpFactory
				.getSwengQueryQuestions());
		String jsonStringQuestions = null;
		try {
			postQuery.setEntity(new StringEntity(query.toJSON().toString()));
			postQuery.setHeader("Content-type", "application/json");
			HttpResponse response = SwengHttpClientFactory.getInstance()
					.execute(postQuery);
			int httpCodeResponse = response.getStatusLine().getStatusCode();
			
			if (Math.floor(httpCodeResponse/ONE_HUNDRED) == BASE_SERVER_ERRORS) {
				UserPreferences.getInstance()
					.setConnectivityState(ConnectivityState.OFFLINE);
			} else if (httpCodeResponse == HttpStatus.SC_OK) {
				jsonStringQuestions = new BasicResponseHandler()
					.handleResponse(response);
				return new JSONObject(jsonStringQuestions);
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(this.getClass().getName(), "doInBackground(): Entity does "
					+ "not support the local encoding.", e);
			return null;
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getName(), "doInBackground(): Error in the "
					+ "HTTP protocol.", e);
			UserPreferences.getInstance()
					.setConnectivityState(ConnectivityState.OFFLINE);
			return null;
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "doInBackground(): An I/O error"
					+ "has occurred.", e);
			UserPreferences.getInstance()
					.setConnectivityState(ConnectivityState.OFFLINE);
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
