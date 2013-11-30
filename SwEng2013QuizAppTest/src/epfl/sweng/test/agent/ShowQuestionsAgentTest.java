package epfl.sweng.test.agent;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.agents.OnlineQuestionsAgent;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.INetworkCommunication;

public class ShowQuestionsAgentTest extends TestCase {

	private OnlineQuestionsAgent mAgent;
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

	public void testAgentNextQuery() {
		mAgent = new OnlineQuestionsAgent(new QuizQuery("randomquery", ""));
		mAgent.setNetworkCommunication(mNetworkComm);
	
		QuizQuestion quizQuestion = mAgent.getNextQuestion();
	
		assertTrue(quizQuestion.getOwner().equals("fruitninja"));
	
		quizQuestion = mAgent.getNextQuestion();
	
		assertTrue(quizQuestion.getOwner().equals("alice"));
	}

	public void testAgentNullQuery() {
		mAgent = new OnlineQuestionsAgent(null);
		mAgent.setNetworkCommunication(mNetworkComm);
	
		QuizQuestion quizQuestion = mAgent.getNextQuestion();
	
		assertTrue(quizQuestion.getOwner().equals("sweng"));
	}

	public void testAgentQueryEmptyFrom() {
		mAgent = new OnlineQuestionsAgent(new QuizQuery("randomquery", ""));
		mAgent.setNetworkCommunication(mNetworkComm);

		QuizQuestion quizQuestion = mAgent.getNextQuestion();

		assertTrue(quizQuestion.getOwner().equals("fruitninja"));
	}

	public void testAgentQueryNullFrom() {
		mAgent = new OnlineQuestionsAgent(new QuizQuery("randomquery", null));
		mAgent.setNetworkCommunication(mNetworkComm);

		QuizQuestion quizQuestion = mAgent.getNextQuestion();

		assertTrue(quizQuestion == null);
	}

	public void testAgentWrongJSONStructure() {
		mAgent = new OnlineQuestionsAgent(new QuizQuery("randomquery", ""));
		mNetworkComm = new INetworkCommunication() {
			
			@Override
			public int sendQuizQuestion(QuizQuestion question) {
				return 0;
			}
			
			@Override
			public QuizQuestion retrieveRandomQuizQuestion() {
				return null;
			}
			
			@Override
			public JSONObject retrieveQuizQuestions(QuizQuery query) {
				JSONObject jsonResponse = null;
				try {
					jsonResponse = new JSONObject("{\"abcdefg\": \"0123456\"}");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return jsonResponse;
			}
		};
		mAgent.setNetworkCommunication(mNetworkComm);
		QuizQuestion question = mAgent.getNextQuestion();
		
		assertTrue(question == null);
	}
}
