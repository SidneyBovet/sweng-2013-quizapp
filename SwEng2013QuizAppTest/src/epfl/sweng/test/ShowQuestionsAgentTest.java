package epfl.sweng.test;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import epfl.sweng.backend.QuizQuery;
import epfl.sweng.comm.IQuestionCommunication;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.showquestions.ShowQuestionsAgent;

public class ShowQuestionsAgentTest extends TestCase {

	private ShowQuestionsAgent mAgent;
	private IQuestionCommunication mNetworkComm;

	public void setUp() {

		try {
			final JSONObject randomQuestionJSON = new JSONObject(
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

			mNetworkComm = new IQuestionCommunication() {

				boolean isFirst = true;
				
				@Override
				public int sendQuizQuestion(QuizQuestion question) {
					return HttpStatus.SC_CREATED;
				}

				@Override
				public JSONObject retrieveRandomQuizQuestion() {
					return randomQuestionJSON;
				}

				@Override
				public JSONObject retrieveQuizQuestion(QuizQuery query) {
					if (isFirst) {
						isFirst = false;
						return firstQueriedJSON;
					} else {
						return secondQueriedJSON;
					}
				}

				@Override
				public void close() {
					
				}
			};
			
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "Problem when hard " +
					"creating JSONObject", e);
			fail("Exception when hard creating JSONobject");
		}

	}

	public void testAgentNextQuery() {
		mAgent = new ShowQuestionsAgent(new QuizQuery("randomquery", ""));
		mAgent.setNetworkCommunication(mNetworkComm);
	
		QuizQuestion quizQuestion = mAgent.getNextQuestion();
	
		assertTrue(quizQuestion.getOwner().equals("fruitninja"));
	
		quizQuestion = mAgent.getNextQuestion();
	
		assertTrue(quizQuestion.getOwner().equals("alice"));
	}

	public void testAgentNullQuery() {
		mAgent = new ShowQuestionsAgent(null);
		mAgent.setNetworkCommunication(mNetworkComm);
	
		QuizQuestion quizQuestion = mAgent.getNextQuestion();
	
		assertTrue(quizQuestion.getOwner().equals("sweng"));
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

	public void testAgentWrongJSONStructure() {
		mAgent = new ShowQuestionsAgent(new QuizQuery("randomquery", ""));
		mNetworkComm = new IQuestionCommunication() {
			
			@Override
			public int sendQuizQuestion(QuizQuestion question) {
				return 0;
			}
			
			@Override
			public JSONObject retrieveRandomQuizQuestion() {
				return null;
			}
			
			@Override
			public JSONObject retrieveQuizQuestion(QuizQuery query) {
				JSONObject jsonResponse = null;
				try {
					jsonResponse = new JSONObject("{\"abcdefg\": \"0123456\"}");
				} catch (JSONException e) {
					Log.e(this.getClass().getName(), "Problem when hard " +
							"creating JSONObject", e);
					fail("Exception when hard creating JSONobject");
				}
				return jsonResponse;
			}

			@Override
			public void close() {
				
			}
		};
		mAgent.setNetworkCommunication(mNetworkComm);
		QuizQuestion question = mAgent.getNextQuestion();
		
		assertTrue(question == null);
	}
}
