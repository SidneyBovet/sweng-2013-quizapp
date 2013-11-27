package epfl.sweng.test;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.backend.QuizQuery;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.INetworkCommunication;
import epfl.sweng.showquestions.ShowQuestionsAgent;

public class ShowQuestionsAgentTest extends TestCase {

	private ShowQuestionsAgent mAgent;
	private INetworkCommunication mNetworkComm;

	public void setUp() {

		try {
			final QuizQuestion randomQuestion = new QuizQuestion(
					"{"
						+ "\"question\": \"What is the answer to life, the universe, and everything?\","
						+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"],"
						+ " \"owner\": \"sweng\","
						+ " \"solutionIndex\": 0,"
						+ " \"tags\": [\"h2g2\", \"trivia\"],"
						+ " \"id\": \"1\""
					+ "}");

			final JSONObject firstQueriedJSON = new JSONObject(
					"{"
						+ "\"questions\": ["
							+ "{" + "\"id\": \"7654765\","
								+ "\"owner\": \"fruitninja\","
								+ "\"question\": \"How many calories are in a banana?\","
								+ "\"answers\": [ \"Just enough\", \"Too many\" ],"
								+ "\"solutionIndex\": 0,"
								+ "\"tags\": [ \"fruit\", \"banana\", \"trivia\" ]"
							+ "},"
						+ "],"
						+ "\"next\": \"YG9HB8)H9*-BYb88fdsfsyb(08bfsdybfdsoi4\""
					+ "}");

			final JSONObject secondQueriedJSON = new JSONObject(
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
						
					+ "}");

			mNetworkComm = new INetworkCommunication() {

				boolean isFirst = true;
				
				@Override
				public int sendQuizQuestion(QuizQuestion question) {
					return HttpStatus.SC_CREATED;
				}

				@Override
				public QuizQuestion retrieveRandomQuizQuestion() {
					return randomQuestion;
				}

				@Override
				public JSONObject retrieveQuizQuestions(QuizQuery query) {
					if (isFirst) {
						isFirst = false;
						return firstQueriedJSON;
					} else {
						return secondQueriedJSON;
					}
				}
			};
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void testAgentQueryEmptyFrom() {
		mAgent = new ShowQuestionsAgent(new QuizQuery("randomquery", ""));
		mAgent.setNetworkCommunication(mNetworkComm);

		QuizQuestion quizQuestion = mAgent.getNextQuestion();

		assertTrue(quizQuestion.getOwner().equals("fruitninja"));
	}

	public void testAgentQueryNullFrom() {
		mAgent = new ShowQuestionsAgent(new QuizQuery("randomquery", null));
		mAgent.setNetworkCommunication(mNetworkComm);

		QuizQuestion quizQuestion = mAgent.getNextQuestion();

		assertTrue(quizQuestion == null);
	}

	public void testAgentNullQuery() {
		mAgent = new ShowQuestionsAgent(null);
		mAgent.setNetworkCommunication(mNetworkComm);

		QuizQuestion quizQuestion = mAgent.getNextQuestion();

		assertTrue(quizQuestion.getOwner().equals("sweng"));
	}

	public void testAgentNextQuery() {
		mAgent = new ShowQuestionsAgent(new QuizQuery("randomquery", ""));
		mAgent.setNetworkCommunication(mNetworkComm);

		QuizQuestion quizQuestion = mAgent.getNextQuestion();

		assertTrue(quizQuestion.getOwner().equals("fruitninja"));

		quizQuestion = mAgent.getNextQuestion();

		assertTrue(quizQuestion.getOwner().equals("alice"));
	}
}
