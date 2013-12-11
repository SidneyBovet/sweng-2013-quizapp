package epfl.sweng.test.servercomm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import epfl.sweng.backend.QuizQuery;
import epfl.sweng.comm.OnlineCommunication;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.AdvancedMockHttpClient;

public class NetworkCommunicationTest extends TestCase {

	private OnlineCommunication mNetworkComm;
	private AdvancedMockHttpClient mClient;
	
	@Override
	public void setUp() {
		mNetworkComm = new OnlineCommunication();
		mClient = new AdvancedMockHttpClient();
		SwengHttpClientFactory.setInstance(mClient);
	}
	
	@Override
	public void tearDown() {
		SwengHttpClientFactory.setInstance(null);
	}
	
	public void testSendQuizQuestionSuccessful() {
		mClient.pushCannedResponse("POST https://sweng-quiz.appspot.com",
				HttpStatus.SC_CREATED, "", "application/json");
		
		QuizQuestion question = new QuizQuestion("My question", new ArrayList<String>(
				Arrays.asList("Answer1", "Answer2", "Answer3")), 0, new TreeSet<String>(
						Arrays.asList("tag1", "tag2")));
		
		int httpResponse = mNetworkComm.sendQuizQuestion(question);
		
		assertEquals("Could not send the question to the mock client properly.",
				httpResponse, HttpStatus.SC_CREATED);
	}
	
	public void testSendQuizQuestionIOException() {
		mClient.pushCannedResponse("POST https://sweng-quiz.appspot.com",
				AdvancedMockHttpClient.IOEXCEPTION_ERROR_CODE, "", "");
		
		QuizQuestion question = new QuizQuestion("My question", new ArrayList<String>(
				Arrays.asList("Answer1", "Answer2", "Answer3")), 0, new TreeSet<String>(
						Arrays.asList("tag1", "tag2")));
		
		int httpResponse = mNetworkComm.sendQuizQuestion(question);
		
		assertEquals("Didn't retrieve the right http error code.",
				httpResponse, HttpStatus.SC_BAD_GATEWAY);
	}
	
	public void testSendQuizQuestionClientProtocolException() {
		mClient.pushCannedResponse("POST https://sweng-quiz.appspot.com",
				AdvancedMockHttpClient.CLIENTPROTOCOLEXCEPTION_ERROR_CODE, "", "");
		
		QuizQuestion question = new QuizQuestion("My question", new ArrayList<String>(
				Arrays.asList("Answer1", "Answer2", "Answer3")), 0, new TreeSet<String>(
						Arrays.asList("tag1", "tag2")));
		
		int httpResponse = mNetworkComm.sendQuizQuestion(question);
		
		assertEquals("Didn't retrieve the right http error code.",
				httpResponse, HttpStatus.SC_BAD_GATEWAY);
	}
	
	public void testSendQuizQuestionBaseServerError() {
		mClient.pushCannedResponse("POST https://sweng-quiz.appspot.com",
				HttpStatus.SC_BAD_GATEWAY, "", "application/json");
		
		QuizQuestion question = new QuizQuestion("My question", new ArrayList<String>(
				Arrays.asList("Answer1", "Answer2", "Answer3")), 0, new TreeSet<String>(
						Arrays.asList("tag1", "tag2")));
		
		int httpResponse = mNetworkComm.sendQuizQuestion(question);
		
		assertEquals("Didn't retrieve the right http error code.",
				httpResponse, HttpStatus.SC_BAD_GATEWAY);
	}
	
	public void testRetrieveRandomQuizQuestionSuccessful() {
		mClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				HttpStatus.SC_OK,
				"{\"question\": \"What is the answer to life, the universe, and everything?\","
						+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
						+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
				"application/json");
				
		try {
			QuizQuestion question = new QuizQuestion(mNetworkComm.retrieveRandomQuizQuestion().toString());
			assertEquals("Didn't retrieve the right question.",
					question.getOwner(), "sweng");
		} catch (JSONException e) {
			Log.e(QuizQuestion.class.getName(), "constructor of QuizQuestion: "
					+ "JSON input was incorrect.", e);
			fail("Exception when hard creating JSONobject");
		}
	}
	
	public void testRetrieveRandomQuizQuestionIOException() {
		mClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				AdvancedMockHttpClient.IOEXCEPTION_ERROR_CODE, "", "");
				
		JSONObject jsonQuestion = mNetworkComm.retrieveRandomQuizQuestion();
		assertEquals("The question retrieved should be null.",
				jsonQuestion, null);
	}
	
	public void testRetrieveRandomQuizQuestionClientProtocolException() {
		mClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				AdvancedMockHttpClient.CLIENTPROTOCOLEXCEPTION_ERROR_CODE, "", "");
				
		JSONObject jsonQuestion = mNetworkComm.retrieveRandomQuizQuestion();
		assertEquals("The question retrieved should be null.",
				jsonQuestion, null);
	}
	
	public void testRetrieveRandomQuizQuestionExceptionBaseServerError() {
		mClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				AdvancedMockHttpClient.CLIENTPROTOCOLEXCEPTION_ERROR_CODE, "", "");
				
		JSONObject jsonQuestion = mNetworkComm.retrieveRandomQuizQuestion();
		assertEquals("The question retrieved should be null.",
				jsonQuestion, null);
	}
	
	public void testRetrieveRandomQuizQuestionWrongJSON() {
		mClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				HttpStatus.SC_OK,
				"not a single structure of JSON question was here",
				"application/json");
				
		JSONObject jsonQuestion = mNetworkComm.retrieveRandomQuizQuestion();
		assertEquals("The question retrieved should be null.",
				jsonQuestion, null);
	}
	
	public void testRetrieveQuizQuerySuccessful() {
		mClient.pushCannedResponse(
				"POST (?:https?://[^/]+|[^/]+)?/+search\\b",
				HttpStatus.SC_OK,
				"{"
						+ "\"questions\": ["
							+ "{"
								+ "\"id\": \"7654765\","
								+ "\"owner\": \"fruitninja\","
								+ "\"question\": \"How many calories are in a banana?\","
								+ "\"answers\": [ \"Just enough\", \"Too many\" ],"
								+ "\"solutionIndex\": 0,"
								+ "\"tags\": [ \"fruit\", \"banana\", \"trivia\" ]"
							+ "},"
						+ "],"
						+ "\"next\": \"YG9HB8)H9*-BYb88fdsfsyb(08bfsdybfdsoi4\""
				+ "}", "application/json");
		
		JSONObject jsonObject = mNetworkComm.retrieveQuizQuestion(
				new QuizQuery("please", "killme"));
		
		try {
			assertEquals("Didn't retrieve the right question.",
					jsonObject.get("next"), "YG9HB8)H9*-BYb88fdsfsyb(08bfsdybfdsoi4");
		} catch (JSONException e) {
			Log.e(QuizQuestion.class.getName(), "get()"
					+ "JSON object was incorrect.", e);
			assertFalse("JSON retrieved was not the same as pushed.", true);
		}
	}
	
	public void testRetrieveQuizQueryClientProtocolException() {
		mClient.pushCannedResponse(
				"POST (?:https?://[^/]+|[^/]+)?/+search\\b",
				AdvancedMockHttpClient.CLIENTPROTOCOLEXCEPTION_ERROR_CODE, "", "");
		
		JSONObject jsonObject = mNetworkComm.retrieveQuizQuestion(
				new QuizQuery("please", "killme"));
		
		assertEquals("The question retrieved should be null.",
				jsonObject, null);
	}
	
	public void testRetrieveQuizQueryIOException() {
		mClient.pushCannedResponse(
				"POST (?:https?://[^/]+|[^/]+)?/+search\\b",
				AdvancedMockHttpClient.IOEXCEPTION_ERROR_CODE, "", "");
		
		JSONObject jsonObject = mNetworkComm.retrieveQuizQuestion(
				new QuizQuery("please", "killme"));
		
		assertEquals("The question retrieved should be null.",
				jsonObject, null);
	}
	
	public void testRetrieveQuizQueryWrongJSON() {
		mClient.pushCannedResponse(
				"POST (?:https?://[^/]+|[^/]+)?/+search\\b",
				HttpStatus.SC_OK,
				"not a single structure of JSON question was here",
				"application/json");
		
		JSONObject jsonObject = mNetworkComm.retrieveQuizQuestion(
				new QuizQuery("please", "killme"));
		
		assertEquals("The question retrieved should be null.",
				jsonObject, null);
	}
}
