package epfl.sweng.comm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import epfl.sweng.backend.Converter;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.HttpFactory;
import epfl.sweng.servercomm.SwengHttpClientFactory;

public class OnlineCommunication implements IQuestionCommunication {

	private static final int BASE_SERVER_ERRORS = 5;
	private static final double ONE_HUNDRED = 100.0;
	private int mHttpStatusCommFailure = HttpStatus.SC_BAD_GATEWAY;
	private CacheContentProvider mContentProvider;

	public OnlineCommunication() {
		mContentProvider = new CacheContentProvider(true);
	}

	/**
	 * Sends a {@link QuizQuestion} to the SwEng server.
	 */
	@Override
	public int sendQuizQuestion(QuizQuestion quizQuestion) {
		mContentProvider.addQuizQuestion(quizQuestion);
		HttpPost postQuery = HttpFactory.getPostRequest(HttpFactory
				.getSwengBaseAddress() + "/quizquestions/");

		int httpCodeResponse = -1;
		try {
			JSONObject jsonQuestion = quizQuestion.toJSON();
			if (jsonQuestion == null) {
				return httpCodeResponse;
			}
			postQuery.setEntity(new StringEntity(jsonQuestion.toString()));
			postQuery.setHeader("Content-type", "application/json");
			HttpResponse response = SwengHttpClientFactory.getInstance()
					.execute(postQuery);
			httpCodeResponse = response.getStatusLine().getStatusCode();

			if (Math.floor(httpCodeResponse / ONE_HUNDRED) == BASE_SERVER_ERRORS) {
				UserPreferences.getInstance().setConnectivityState(
						ConnectivityState.OFFLINE);
			}
			if (response.getEntity() != null) {
				response.getEntity().consumeContent();
			}

			return httpCodeResponse;

		} catch (UnsupportedEncodingException e) {
			Log.e(this.getClass().getName(), "sendQuizQuestion(): Entity does "
					+ "not support the local encoding.", e);
			return mHttpStatusCommFailure;
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getName(),
					"sendQuizQuestion(): Error in the " + "HTTP protocol.", e);
			UserPreferences.getInstance().setConnectivityState(
					ConnectivityState.OFFLINE);
			return mHttpStatusCommFailure;
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "sendQuizQuestion(): An I/O error"
					+ " has occurred.", e);
			UserPreferences.getInstance().setConnectivityState(
					ConnectivityState.OFFLINE);
			return mHttpStatusCommFailure;
		}
	}

	/**
	 * Retrieves a {@link QuizQuestion} from the SwEng server, according to a
	 * specific {@link QuizQuery}.
	 */
	@Override
	public JSONObject retrieveQuizQuestion(QuizQuery quizQuery) {
		HttpPost postQuery = HttpFactory.getPostRequest(HttpFactory
				.getSwengQueryQuestions());
		String jsonStringQuestions = null;
		try {
			postQuery
					.setEntity(new StringEntity(quizQuery.toJSON().toString()));
			postQuery.setHeader("Content-type", "application/json");
			HttpResponse response = SwengHttpClientFactory.getInstance()
					.execute(postQuery);
			int httpCodeResponse = response.getStatusLine().getStatusCode();

			if (Math.floor(httpCodeResponse / ONE_HUNDRED) == BASE_SERVER_ERRORS) {
				UserPreferences.getInstance().setConnectivityState(
						ConnectivityState.OFFLINE);
				return null;

			} else if (httpCodeResponse == HttpStatus.SC_OK) {
				jsonStringQuestions = new BasicResponseHandler()
						.handleResponse(response);
				JSONObject jsonQuestions = new JSONObject(jsonStringQuestions);

				// cache the questions retrieved
				JSONArray array = jsonQuestions.getJSONArray("questions");
				if (array != null) {
					List<QuizQuestion> fetchedQuestions = Converter
							.jsonArrayToQuizQuestionList(array);

					for (QuizQuestion question : fetchedQuestions) {
						if (question != null && question.auditErrors() == 0) {
							mContentProvider.addQuizQuestion(question);
						}
					}
				}

				return jsonQuestions;
			} else {
				return null;
			}

		} catch (UnsupportedEncodingException e) {
			Log.e(this.getClass().getName(), "retrieveQuizQuestion(): Entity "
					+ "does not support the local encoding.", e);
			return null;
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getName(),
					"retrieveQuizQuestion(): Error in " + "the HTTP protocol.",
					e);
			UserPreferences.getInstance().setConnectivityState(
					ConnectivityState.OFFLINE);
			return null;
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "retrieveQuizQuestion(): An I/O "
					+ "error has occurred.", e);
			UserPreferences.getInstance().setConnectivityState(
					ConnectivityState.OFFLINE);
			return null;
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "retrieveQuizQuestion(): "
					+ "QuizQuestion JSON input was incorrect.", e);
			return null;
		}
	}

	/**
	 * Retrieves a random {@link QuizQuestion} from the SwEng Server.
	 */
	@Override
	public JSONObject retrieveRandomQuizQuestion() {
		String jsonQuestion = null;

		String url = HttpFactory.getSwengFetchQuestion();
		HttpGet firstRandom = HttpFactory.getGetRequest(url);
		try {
			HttpResponse response = SwengHttpClientFactory.getInstance()
					.execute(firstRandom);
			int httpCodeResponse = response.getStatusLine().getStatusCode();

			if (Math.floor(httpCodeResponse / ONE_HUNDRED) == BASE_SERVER_ERRORS) {
				UserPreferences.getInstance().setConnectivityState(
						ConnectivityState.OFFLINE);
				return null;

			} else if (httpCodeResponse == HttpStatus.SC_OK) {
				jsonQuestion = new BasicResponseHandler()
						.handleResponse(response);
				mContentProvider
						.addQuizQuestion(new QuizQuestion(jsonQuestion));
				return new JSONObject(jsonQuestion);

			} else {
				return null;
			}

		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getName(), "retrieveRandomQuizQuestion(): "
					+ "Error in the HTTP protocol.", e);
			UserPreferences.getInstance().setConnectivityState(
					ConnectivityState.OFFLINE);
			return null;
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "retrieveRandomQuizQuestion(): "
					+ "An I/O error has occurred.", e);
			UserPreferences.getInstance().setConnectivityState(
					ConnectivityState.OFFLINE);
			return null;
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "retrieveRandomQuizQuestion(): "
					+ "QuizQuestion JSON input was incorrect.", e);
			return null;
		}
	}

	/**
	 * Closes everything related to the cache content provider.
	 */
	@Override
	public void close() {
		// Nothing to do
	}
}
