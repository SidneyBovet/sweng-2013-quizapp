package epfl.sweng.test.activities;

import org.apache.http.HttpStatus;

import android.widget.Button;
import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

//import java.io.IOException;

/** A test that illustrates the use of MockHttpClients */
// XXX : Tests work separetely, find out why it doesn't work together.
public class ShowQuestionsActivityTest extends GUITest<ShowQuestionsActivity> {

	protected static final String RANDOM_QUESTION_BUTTON_LABEL = "Show a random question";

	private MockHttpClient mockClient;

	public ShowQuestionsActivityTest() {
		super(ShowQuestionsActivity.class);
	}

	@Override
	public void setUp() {
		super.setUp();
		mockClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(mockClient);
		UserPreferences.getInstance(getInstrumentation().getContext()).
			createEntry("CONNECTION_STATE", "ONLINE");
	}

	public void testFetchQuestion() {
		mockClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"What is the answer to life, the universe, and everything?\","
								+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
						"application/json");

		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		assertTrue(
				"Question must be displayed",
				getSolo()
						.searchText(
								"What is the answer to life, the universe, and everything?"));
		assertTrue("Correct answer must be displayed",
				getSolo().searchText("Forty-two"));
		assertTrue("Incorrect answer must be displayed",
				getSolo().searchText("Twenty-seven"));
	}

	public void testCorrectQuestionSelected() {
		mockClient
			.pushCannedResponse(
					"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
					HttpStatus.SC_OK,
					"{\"question\": \"What is the answer to life, the universe, and everything?\","
							+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
							+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
					"application/json");
		mockClient
			.pushCannedResponse(
					"POST (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
					HttpStatus.SC_OK,
					"{\"question\": \"What is the answer to life, the universe, and everything?\","
							+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
							+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
					"application/json");

		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		getSolo().sleep(1000);
		assertTrue("Correct answer must be displayed",
				getSolo().searchText("Forty-two"));
		
		getSolo().clickOnText("Forty-two");
		
		getActivityAndWaitFor(TTChecks.ANSWER_SELECTED);
		assertTrue("Couldn't find the correct answer", getSolo().searchText("" + (char)10004));
	}

	public void testWrongQuestionSelected() {
		mockClient
		.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				HttpStatus.SC_OK,
				"{\"question\": \"What is the answer to life, the universe, and everything?\","
						+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
						+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
				"application/json");

		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		assertTrue("Correct answer must be displayed",
				getSolo().searchText("Forty-two"));
		assertTrue("Wrong answer must be displayed",
				getSolo().searchText("Twenty-seven"));
		
		getSolo().clickOnText("Twenty-seven");
		
		getActivityAndWaitFor(TTChecks.ANSWER_SELECTED);
		assertTrue("Couldn't find the correct answer", getSolo().searchText("" + (char)10008));

	}

	public void testNextButtonBehaviour() {
		mockClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"Final question.\","
								+ " \"answers\": [\"#1 Answer\", \"#2 Answer\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 0, \"tags\": [\"tag3\"], \"id\": \"2\" }",
						"application/json");
		mockClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"Question content.\","
								+ " \"answers\": [\"Answer #1\", \"Answer #2\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 1, \"tags\": [\"tag1\", \"tag2\"], \"id\": \"2\" }",
						"application/json");
		mockClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"What is the answer to life, the universe, and everything?\","
								+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
						"application/json");

		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);

		assertTrue("Correct answer must be displayed",
				getSolo().searchText("Forty-two"));
		getSolo().clickOnText("Forty-two");
		
		getActivityAndWaitFor(TTChecks.ANSWER_SELECTED);
		
		assertTrue("Couldn't find the correct answer", getSolo().searchText("" + (char)10004));
		mockClient.popCannedResponse();
		getSolo().clickOnButton("Next question");
		
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);

		assertTrue("Question must be displayed",
				getSolo().searchText("Question content."));
		
		assertTrue("Wrong answer must be displayed",
				getSolo().searchText("Answer #1"));
		getSolo().clickOnText("Answer #1");

		getActivityAndWaitFor(TTChecks.ANSWER_SELECTED);

		assertTrue("Couldn't find the wrong answer",getSolo().
				searchText("" + (char)10008));
		
		assertTrue("Correct answer must be displayed",
				getSolo().searchText("Answer #2"));
		getSolo().clickOnText("Answer #2");
		
		getActivityAndWaitFor(TTChecks.ANSWER_SELECTED);

		assertTrue("Couldn't find the correct answer", getSolo().searchText("" + (char)10004));
		mockClient.popCannedResponse();
		getSolo().clickOnButton("Next question");
		
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);

		assertTrue("Question must be displayed",
				getSolo().searchText("Final question."));
	}

	public void testNextButtonFirstDisabled() {
		mockClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"What is the answer to life, the universe, and everything?\","
								+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
						"application/json");
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		assertTrue("Button next is present",
				getSolo().searchText("Next question"));
		Button nextButton = getSolo().getButton("Next question");
		assertFalse(nextButton.isEnabled());
	}

	public void testExceptionWhileFetchingQuestionIsHandled() {
		mockClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				MockHttpClient.IOEXCEPTION_ERROR_CODE,
				"", "");
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		assert true;
	}

	public void testErrorWhile503SendByServer() {
		mockClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				HttpStatus.SC_SERVICE_UNAVAILABLE,
				"", "");
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		getSolo().searchText("There was an error retrieving the question");
	}

	public void testErrorWhile500SendByServer() {
		mockClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				MockHttpClient.IOEXCEPTION_ERROR_CODE,
				"", "");
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		getSolo().searchText("There was an error retrieving the question");
	}
	
	//TODO debug test

	public void testErrorWhile400SendByServer() {
		mockClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				MockHttpClient.FORBIDDEN_ERROR_CODE,
				"", "");
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		getSolo().searchText("There was an error retrieving the question");
	}
	
}
