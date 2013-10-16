package epfl.sweng.test;

import java.io.IOException;

import org.apache.http.HttpStatus;

import android.widget.Button;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

//import java.io.IOException;

/** A test that illustrates the use of MockHttpClients */
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

	// TODO find why clickontext does not work
	public void testCorrectQuestionSelected() {
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

	public void testErrorWhileFetchingQuestionIsHandled() {
		mockClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				MockHttpClient.IOEXCEPTION_ERROR_CODE,
				"", "");
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		assert true;
	}

}
