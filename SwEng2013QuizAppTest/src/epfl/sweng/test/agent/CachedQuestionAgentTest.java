/*package epfl.sweng.test.agent;

import org.json.JSONException;

import android.content.Context;
import epfl.sweng.agents.CachedQuestionAgent;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.test.activities.GUITest;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class CachedQuestionAgentTest extends GUITest<MainActivity> {
	private CachedQuestionAgent fakeCachedAgent;
	private QuizQuery fakeQueryCache;
	private Context contextShowQuestionActivity;
	private CacheContentProvider mContentProvider;
	private QuizQuestion mQuestion;
	
	public CachedQuestionAgentTest() {
		super(MainActivity.class);
	}
	@Override
	protected void setUp() {
		//TODO clean DB
		contextShowQuestionActivity = getInstrumentation()
				.getTargetContext();
		fakeQueryCache = new QuizQuery("queryOffline", "from");
		fakeCachedAgent = new CachedQuestionAgent(fakeQueryCache, 
				contextShowQuestionActivity);
		mContentProvider = new CacheContentProvider(
				contextShowQuestionActivity, false);
		mContentProvider.eraseDatabase();
		try {
			mQuestion = new QuizQuestion(
					"{"
						+ "\"question\": \"What is the answer to life, the universe, and everything?\","
						+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"],"
						+ " \"owner\": \"sweng\","
						+ " \"solutionIndex\": 0,"
						+ " \"tags\": [\"h2g2\", \"trivia\"],"
						+ " \"id\": \"1\""
					+ "}");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mContentProvider.addQuizQuestion(mQuestion);
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		fakeCachedAgent.close();
		mContentProvider.close();
	}

	public void testClose() {
		fakeCachedAgent.close();
		assertTrue(fakeCachedAgent.isClosed());
	}
	
	public void testGetNextQuestion() {
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		getSolo().clickOnMenuItem("Erase database"); 
		assertEquals(mQuestion, fakeCachedAgent.getNextQuestion());
	}
}*/
