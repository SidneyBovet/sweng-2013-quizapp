package epfl.sweng.test.agent;

import android.test.AndroidTestCase;
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

	@Override
	protected void setUp() {
		
		try {
			super.setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		fakeQueryOnline = new QuizQuery("queryOnline", "from");
		fakeQueryCache = new QuizQuery("queryOffline", "from");
		fakeCachedAgent = new CachedQuestionAgent(fakeQueryCache);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		fakeCachedAgent.close();
	}

	public void testInstance() {
		QuestionAgentFactory.setInstance(fakeCachedAgent);
		QuestionAgent agentToTest = QuestionAgentFactory.getAgent(fakeQueryCache);
		assertEquals(fakeCachedAgent, agentToTest);
		QuestionAgentFactory.releaseInstance();
	}

	public void testGetAgent() {
		
		UserPreferences.getInstance()
		.setConnectivityState(ConnectivityState.OFFLINE);
		
		QuestionAgent agentOfflineToTest = QuestionAgentFactory.getAgent(fakeQueryCache);
		assertTrue(agentOfflineToTest instanceof CachedQuestionAgent);
		QuestionAgentFactory.releaseInstance();

		UserPreferences.getInstance().setConnectivityState(ConnectivityState.ONLINE);
		QuestionAgent agentOnlineToTest = QuestionAgentFactory.getAgent(fakeQueryOnline);
		assertTrue(fakeQueryOnline.equals(agentOnlineToTest.getQuery()));
		assertTrue(agentOnlineToTest instanceof OnlineQuestionsAgent);
		QuestionAgentFactory.releaseInstance();

		QuestionAgentFactory.setInstance(fakeCachedAgent);
		QuestionAgent agentToTest = QuestionAgentFactory.getAgent(fakeQueryCache);
		assertTrue(agentToTest instanceof CachedQuestionAgent);
	}
	
	
}