package epfl.sweng.test.proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.database.Cursor;
import epfl.sweng.agents.OnlineQuestionsAgent;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.caching.SQLiteCacheHelper;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.patterns.ConnectivityState;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.activities.GUITest;
import epfl.sweng.test.minimalmock.MockHttpClient;

public class QuestionProxyTest extends GUITest<MainActivity>{	
	
	public QuestionProxyTest() {
		super(MainActivity.class);
	}

	private QuestionsProxy proxy;
	private QuizQuestion mQuestion;
	private Context contextOfMainActivity;
	private CacheContentProvider mContentProvider;
	private MockHttpClient mMockClient;
	
	@Override
	protected void setUp() {
		super.setUp();
		contextOfMainActivity = getInstrumentation()
				.getTargetContext();
		mContentProvider = new CacheContentProvider(
				contextOfMainActivity, true);
		mContentProvider.eraseDatabase();
		mMockClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(mMockClient);
		proxy = QuestionsProxy.getInstance(contextOfMainActivity);
		mQuestion = new QuizQuestion("q", 
				new ArrayList<String>(Arrays.asList("a1", "a2", "a3")), 
				0, new TreeSet<String>(Arrays.asList("t1", "t2")), 1, "o");
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		/*if(!mContentProvider.isClosed()){
			mContentProvider.close();
		}*/
	}
	public void testSingleton() {
		QuestionsProxy proxy2 = QuestionsProxy.getInstance(contextOfMainActivity);
		assertTrue(proxy.equals(proxy2));
		QuestionsProxy proxy3 = QuestionsProxy.getInstance();
		assertTrue(proxy2.equals(proxy3));
	} 
	
	public void testAddInbox() {
		proxy.addInbox(mQuestion);
		Cursor cursor = mContentProvider.getQuestions(new QuizQuery());
		int id = cursor.getInt(0);
		QuizQuestion question = mContentProvider.getQuestionFromPK(id);
		getSolo().sleep(500);
		assertEquals("Statement should be the same.",
				"q", question.getStatement());
	}
	
	public void testaddOutAndInbox() {
		proxy.addOutAndInbox(mQuestion);
		Cursor cursor = mContentProvider.getQuestions(new QuizQuery());
		int id = cursor.getInt(0);
		QuizQuestion question = mContentProvider.getQuestionFromPK(id);
		getSolo().sleep(500);
		int isQueued = cursor.getInt(cursor.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_PK));
		assertEquals("Statement should be the same.",
				"q", question.getStatement());
		assertTrue("The question is not queued",isQueued==1);
		//assertTrue("There is not one question in the outbox", proxy.getOutboxSize()==1);
	}
	
	public void testSendQuizQuestion() {
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.OFFLINE);
		assertEquals(HttpStatus.SC_CREATED, proxy.sendQuizQuestion(mQuestion));
		//XXX to decomment when issue #118 will be resolved
		/*UserPreferences.getInstance().setConnectivityState(ConnectivityState.ONLINE);
		assertEquals(HttpStatus.SC_CREATED, proxy.notifyConnectivityChange(ConnectivityState.ONLINE));
		mMockClient.pushCannedResponse(
				"(POST|GET) https://sweng-quiz.appspot.com/quizquestions/",
				200, "", "HttpResponse");
		assertEquals(200, proxy.sendQuizQuestion(mQuestion));*/
	}
	
	public void testRetrieveRandomQuestion(){
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.ONLINE);
		mMockClient
		.pushCannedResponse(
				".+",
				200,
				"{\"id\": 17005,\"question\": \"What is the capital of Antigua and Barbuda?\",\"answers\": [\"Chisinau\"],\"solutionIndex\": 2,\"tags\": [\"capitals\"],\"owner\": \"sweng\"}",
				"application/json");
		assertTrue("The question was not fetched", proxy.retrieveRandomQuizQuestion().getId()==17005);
	}
	
	public void testNotifyConnectivityChange() {
		assertEquals(HttpStatus.SC_OK, proxy.notifyConnectivityChange(ConnectivityState.OFFLINE));
		assertEquals(HttpStatus.SC_CREATED, proxy.notifyConnectivityChange(ConnectivityState.ONLINE));
		//XXX to decomment when issue #118 will be resolved
		/*mMockClient.pushCannedResponse(
				"(POST|GET) https://sweng-quiz.appspot.com/quizquestions/",
				200, "", "HttpResponse");
		proxy.addOutAndInbox(mQuestion);
		assertTrue("Outbox doesn't contain one element", proxy.getOutboxSize()==1);
		assertEquals("The sending of cached question was not successful",200, proxy.notifyConnectivityChange(ConnectivityState.ONLINE));*/
	}
	
	public void testSetStream(){
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.ONLINE);
		OnlineQuestionsAgent agent = new OnlineQuestionsAgent(null);
		proxy.setStream(null);
		
	}

}
