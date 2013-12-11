package epfl.sweng.test.activities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpStatus;

import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.comm.ConnectivityState;
import epfl.sweng.comm.QuestionProxy;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.test.minimalmock.UnconnectedHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

//import java.io.IOException;

/** A test that illustrates the use of MockHttpClients */
public class ShowQuestionsActivityOfflineTest extends
		GUITest<ShowQuestionsActivity> {

	protected static final String RANDOM_QUESTION_BUTTON_LABEL = "Show a random question";
	private QuestionProxy mProxy;
	
	MockHttpClient mMockClient;
	UnconnectedHttpClient mUnconnectedClient;
	CacheContentProvider cacheContentProvider;

	public ShowQuestionsActivityOfflineTest() {
		super(ShowQuestionsActivity.class);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		SwengHttpClientFactory.setInstance(null);
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.ONLINE);
		QuestionProxy.resetQuestionProxy();
		//cacheContentProvider.close();
	}

	@Override
	public void setUp() {
		//cacheContentProvider = new CacheContentProvider(true);
		super.setUp();

		/* Reseting both client for security */
		CacheContentProvider contentProvider = new CacheContentProvider(false);
		contentProvider.eraseDatabase();
		mUnconnectedClient = new UnconnectedHttpClient();
		mMockClient = new MockHttpClient();

		List<String> answers = new ArrayList<String>();
		answers.add("100 percent accurate");
		answers.add("Fully voodoo and could generate non-pseudorandom numbers");

		Set<String> tags = new HashSet<String>();
		tags.add("robotium");
		tags.add("testing");

		QuizQuestion question = new QuizQuestion(
				"How reliable Robotium testing is?", answers, 1, tags);
		mProxy = QuestionProxy.getInstance();
		mProxy.notifyConnectivityChange(ConnectivityState.OFFLINE);
		mProxy.sendQuizQuestion(question);
	}

	public void testQuestionInInboxIsDisplayedWhenOffline() {
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.OFFLINE);
		SwengHttpClientFactory.setInstance(mUnconnectedClient);

		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		assertTrue(
				"Question must be displayed",
				getSolo().searchText(
						"How\\ reliable\\ Robotium\\ testing\\ is\\?"));
		assertTrue("Incorrect answer must be displayed",
				getSolo().searchText("100\\ percent\\ accurate"));
		assertTrue("Correct answer must be displayed", getSolo().searchText(
				"Fully\\ voodoo\\ and\\ could\\ generate\\ non\\-"
						+ "pseudorandom\\ numbers"));
	}

	public void testNewlyRetrievedQuestionShouldBeCached() {
		//int expectedInboxSize = cacheContentProvider.;

		UserPreferences.getInstance().setConnectivityState(ConnectivityState.ONLINE);
		SwengHttpClientFactory.setInstance(mMockClient);
		mMockClient
				.pushCannedResponse(
						"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
						HttpStatus.SC_OK,
						"{\"question\": \"What is the answer to life, the universe, and everything?\","
								+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
								+ " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
						"application/json");

		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		getSolo().sleep(500);
		//assertEquals(expectedInboxSize, QuestionsProxy.getInstance()
		//		.getInboxSize());
	}

	public void testNetworkUnavailableShouldMakeConnectionStateOffline() {
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.ONLINE);
		SwengHttpClientFactory.setInstance(mUnconnectedClient);
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		getSolo().sleep(500);
		boolean isOnline = UserPreferences.getInstance().isConnected();
		assertEquals("After a failed connection, state should be offline",
				false, isOnline);
	}

	public void testStatus500ShouldMakeConnectionStateOffline() {
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.ONLINE);
		SwengHttpClientFactory.setInstance(mMockClient);

		mMockClient.pushCannedResponse(".",
				HttpStatus.SC_INTERNAL_SERVER_ERROR, "", "");

		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		getSolo().sleep(500);
		boolean isOnline = UserPreferences.getInstance().isConnected();
		assertEquals("After a failed connection, state should be offline",
				false, isOnline);
	}
}
