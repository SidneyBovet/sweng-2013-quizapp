package epfl.sweng.test.agent;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import epfl.sweng.agents.CachedQuestionAgent;
import epfl.sweng.agents.OnlineQuestionsAgent;
import epfl.sweng.agents.QuestionAgent;
import epfl.sweng.agents.QuestionAgentFactory;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.patterns.ConnectivityState;
import epfl.sweng.preferences.UserPreferences;

public class QuestionAgentFactoryTest extends AndroidTestCase {
	private QuizQuery fakeQueryOnline;
	private QuizQuery fakeQueryCache;
	private CachedQuestionAgent fakeCachedAgent;
	private Context contextShowQuestionActivity;

	@Override
	protected void setUp() {
		
		try {
			super.setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		contextShowQuestionActivity = new RenamingDelegatingContext(
				getContext(), "test_");
		
		fakeQueryOnline = new QuizQuery("queryOnline", "from");
		fakeQueryCache = new QuizQuery("queryOffline", "from");
		fakeCachedAgent = new CachedQuestionAgent(fakeQueryCache,
				contextShowQuestionActivity);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		fakeCachedAgent.close();
	}

	public void testInstance() {
		QuestionAgentFactory.setInstance(fakeCachedAgent);
		QuestionAgent agentToTest = QuestionAgentFactory.getAgent(
				contextShowQuestionActivity, fakeQueryCache);
		assertEquals(fakeCachedAgent, agentToTest);
		QuestionAgentFactory.releaseInstance();
	}

	public void testGetAgent() {
		
		UserPreferences.getInstance(contextShowQuestionActivity)
		.setConnectivityState(ConnectivityState.OFFLINE);
		
		QuestionAgent agentOfflineToTest = QuestionAgentFactory.getAgent(
				contextShowQuestionActivity, fakeQueryCache);
		assertTrue(agentOfflineToTest instanceof CachedQuestionAgent);
		QuestionAgentFactory.releaseInstance();

		UserPreferences.getInstance(contextShowQuestionActivity)
				.setConnectivityState(ConnectivityState.ONLINE);
		QuestionAgent agentOnlineToTest = QuestionAgentFactory.getAgent(
				contextShowQuestionActivity, fakeQueryOnline);
		assertTrue(agentOnlineToTest instanceof OnlineQuestionsAgent);
		QuestionAgentFactory.releaseInstance();

		QuestionAgentFactory.setInstance(fakeCachedAgent);
		QuestionAgent agentToTest = QuestionAgentFactory.getAgent(
				contextShowQuestionActivity, fakeQueryCache);
		assertTrue(agentToTest instanceof CachedQuestionAgent);
	}
}