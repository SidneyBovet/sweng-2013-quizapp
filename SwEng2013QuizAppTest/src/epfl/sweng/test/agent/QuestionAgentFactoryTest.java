/*package epfl.sweng.test.agent;

import android.content.Context;
import epfl.sweng.agents.CachedQuestionAgent;
import epfl.sweng.agents.OnlineQuestionsAgent;
import epfl.sweng.agents.QuestionAgent;
import epfl.sweng.agents.QuestionAgentFactory;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.patterns.ConnectivityState;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.test.activities.GUITest;

public class QuestionAgentFactoryTest extends GUITest<ShowQuestionsActivity> {
	private QuizQuery fakeQueryOnline; 
	private QuizQuery fakeQueryCache;
	private CachedQuestionAgent fakeCachedAgent;
	private Context contextShowQuestionActivity;
	private CacheContentProvider mContentProvider;
	
	
	public QuestionAgentFactoryTest() {
		super(ShowQuestionsActivity.class);
	}

	@Override
	protected void setUp() {
		super.setUp();
		contextShowQuestionActivity = getInstrumentation()
				.getTargetContext();
		mContentProvider = new CacheContentProvider(
				contextShowQuestionActivity, true);
		mContentProvider.eraseDatabase();
		fakeQueryOnline = new QuizQuery("queryOnline", "from");
		fakeQueryCache = new QuizQuery("queryOffline", "from");
		fakeCachedAgent = new CachedQuestionAgent(fakeQueryCache, 
				contextShowQuestionActivity);

	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		mContentProvider.close();
		fakeCachedAgent.close();
	}
	
	public void testInstance() {
		QuestionAgentFactory.setInstance(fakeCachedAgent);
		QuestionAgent agentToTest = 
				QuestionAgentFactory.getAgent(contextShowQuestionActivity, fakeQueryCache);
		assertEquals(fakeCachedAgent, agentToTest);
		QuestionAgentFactory.releaseInstance();
	}
	
	public void testGetAgent() {
		QuestionAgent agentOfflineToTest = QuestionAgentFactory.
				getAgent(contextShowQuestionActivity, fakeQueryCache);
		assertTrue(agentOfflineToTest instanceof CachedQuestionAgent);
		QuestionAgentFactory.releaseInstance();
		
		UserPreferences.getInstance(contextShowQuestionActivity).
			setConnectivityState(ConnectivityState.ONLINE);
		QuestionAgent agentOnlineToTest = QuestionAgentFactory.
				getAgent(contextShowQuestionActivity, fakeQueryOnline);
		assertTrue(agentOnlineToTest instanceof OnlineQuestionsAgent);
		QuestionAgentFactory.releaseInstance();
		
		QuestionAgentFactory.setInstance(fakeCachedAgent);
		QuestionAgent agentToTest = QuestionAgentFactory.
				getAgent(contextShowQuestionActivity, fakeQueryCache);
		assertTrue(agentToTest instanceof CachedQuestionAgent);
	}
}*/
