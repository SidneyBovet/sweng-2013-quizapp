package epfl.sweng.test;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;

import epfl.sweng.backend.QuizQuery;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.showquestions.ShowQuestionsAgent;
import epfl.sweng.test.minimalmock.AdvancedMockHttpClient;

public class ShowQuestionsAgentTest extends TestCase {

	private ShowQuestionsAgent mAgent;

	public void setUp() {
		mAgent = new ShowQuestionsAgent(new QuizQuery("default", "default"));
		AdvancedMockHttpClient mockClient = new AdvancedMockHttpClient();
		
		// Default random question
		mockClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"What is the answer to life, the universe, and everything?\","
								+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
						"application/json");

		// Default next question batch
		mockClient
			.pushCannedResponse(
					"POST (?:https?://[^/]+|[^/]+)?/+search\\b",
					HttpStatus.SC_OK,
					"{"
						+ "\"questions\": ["
							+ "{"
								+ "\"id\": \"1234567890\","
								+ "\"owner\": \"alice\","
								+ "\"question\": \"Are you aware?\","
								+ "\"answers\": [ \"Yes\", \"No\" ],"
								+ "\"solutionIndex\": 1,"
								+ "\"tags\": [ \"aware\", \"alice\" ]"
							+ "},"
						+ "]"
					+ "}",
					"application/json");
		
		// Default queried question batch
		mockClient
				.pushCannedResponse(
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
						+ "}",
						"application/json");
		
		SwengHttpClientFactory.setInstance(mockClient);
	}

	public void testAgentQueryEmptyFrom() {
		mAgent = new ShowQuestionsAgent(new QuizQuery("randomquery", ""));

		QuizQuestion quizQuestion = mAgent.getNextQuestion();
		
		assertTrue(quizQuestion.getOwner().equals("fruitninja"));
	}

	public void testAgentQueryNullFrom() {
		mAgent = new ShowQuestionsAgent(new QuizQuery("randomquery", null));

		QuizQuestion quizQuestion = mAgent.getNextQuestion();
		
		assertTrue(quizQuestion == null);
	}
	
	public void testAgentNullQuery() {
		mAgent = new ShowQuestionsAgent(null);
		
		QuizQuestion quizQuestion = mAgent.getNextQuestion();
		
		assertTrue(quizQuestion.getOwner().equals("sweng"));
	}
	
	public void testAgentNextQuery() {
		mAgent = new ShowQuestionsAgent(new QuizQuery("randomquery", ""));

		QuizQuestion quizQuestion = mAgent.getNextQuestion();
		
		assertTrue(quizQuestion.getOwner().equals("fruitninja"));
		
		quizQuestion = mAgent.getNextQuestion();
		
		assertTrue(quizQuestion.getOwner().equals("alice"));
	}
}
