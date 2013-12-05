package epfl.sweng.test.proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.comm.ConnectivityState;
import epfl.sweng.comm.QuestionProxy;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.activities.GUITest;
import epfl.sweng.test.minimalmock.AdvancedMockHttpClient;
import epfl.sweng.test.minimalmock.MockHttpClient;

public class QuestionProxyTest extends GUITest<MainActivity>{	
	
	public QuestionProxyTest() {
		super(MainActivity.class);
	}

	private QuestionProxy proxy;
	private QuizQuestion mQuestion;
	private CacheContentProvider mContentProvider;
	private MockHttpClient mMockClient;
	private QuizQuery mfakeQuery;
	@Override
	protected void setUp() {
		super.setUp();
		mContentProvider = new CacheContentProvider(true);
		mContentProvider.eraseDatabase();
		mMockClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(mMockClient);
		proxy = QuestionProxy.getInstance();
		mQuestion = new QuizQuestion("q", 
				new ArrayList<String>(Arrays.asList("a1", "a2", "a3")), 
				0, new TreeSet<String>(Arrays.asList("t1", "t2")), 1, "o");
		mfakeQuery = new QuizQuery("queryOnline", "from");
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		/*if(!mContentProvider.isClosed()){
			mContentProvider.close();
		}*/
	}
	public void testSingleton() {
		QuestionProxy proxy2 = QuestionProxy.getInstance();
		assertTrue(proxy.equals(proxy2));
		QuestionProxy proxy3 = QuestionProxy.getInstance();
		assertTrue(proxy2.equals(proxy3));
	} 
	
//	public void testAddInbox() {
//		proxy.addInbox(mQuestion);
//		Cursor cursor = mContentProvider.getQuestions(new QuizQuery());
//		int id = cursor.getInt(0);
//		QuizQuestion question = mContentProvider.getQuestionFromPK(id);
//		getSolo().sleep(500);
//		assertEquals("Statement should be the same.",
//				"q", question.getStatement());
//	}
//	
//	public void testaddOutAndInbox() {
//		proxy.addOutAndInbox(mQuestion);
//		Cursor cursor = mContentProvider.getQuestions(new QuizQuery());
//		int id = cursor.getInt(0);
//		QuizQuestion question = mContentProvider.getQuestionFromPK(id);
//		getSolo().sleep(500);
//		int isQueued = cursor.getInt(cursor.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_PK));
//		assertEquals("Statement should be the same.",
//				"q", question.getStatement());
//		assertTrue("The question is not queued",isQueued==1);
//		//assertTrue("There is not one question in the outbox", proxy.getOutboxSize()==1);
//	}
	
	public void testSendQuizQuestion() {
		/*UserPreferences.getInstance().setConnectivityState(ConnectivityState.OFFLINE);
		assertEquals(HttpStatus.SC_CREATED, proxy.sendQuizQuestion(mQuestion));
		//XXX to decomment when issue #118 will be resolved
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.ONLINE);
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
		QuizQuestion retrievedQuestion = null;
		try {
			retrievedQuestion = new QuizQuestion(proxy.retrieveRandomQuizQuestion().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		assertTrue("The question was not fetched", retrievedQuestion.getId()==17005);
	}
	
	public void testNotifyConnectivityChange() {
		assertEquals(HttpStatus.SC_OK, proxy.notifyConnectivityChange(ConnectivityState.OFFLINE));
		assertEquals(HttpStatus.SC_CREATED, proxy.notifyConnectivityChange(ConnectivityState.ONLINE));
		assertEquals(HttpStatus.SC_OK, proxy.notifyConnectivityChange(ConnectivityState.OFFLINE));
		//XXX to decomment when issue #118 will be resolved
		mMockClient.pushCannedResponse(
				"(POST|GET) https://sweng-quiz.appspot.com/quizquestions/",
				200, "", "HttpResponse");
		proxy.sendQuizQuestion(mQuestion);
		CacheContentProvider provider = new CacheContentProvider(false);
		assertTrue("Outbox doesn't contain one element", provider.getOutboxCount()==1);
		assertEquals("The sending of cached question was not successful",200, proxy.notifyConnectivityChange(ConnectivityState.ONLINE));
		provider.close();
	}
	
	public void testRetrieveQuizQuestionsOffline() {
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.OFFLINE);
		assertEquals(null, proxy.retrieveQuizQuestion(mfakeQuery));
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.ONLINE);
	}
	
	public void testRetrieveQuizQuestionJSONResponseOK(){
		mMockClient.pushCannedResponse(
				"POST (?:https?://[^/]+|[^/]+)?/+search\\b",
                HttpStatus.SC_OK,
                "{\"questions\": [ {\"question\": \"q1\","
                + " \"answers\": [\"a1\", \"b1\"], \"owner\": \"o1\","
                + " \"solutionIndex\": 0, \"tags\": [\"fruit\", \"t1\"], \"id\": \"1\" }, " +
                "{\"question\": \"q2\","
                + " \"answers\": [\"a2\", \"b2\", \"c2\"], \"owner\": \"o2\","
                + " \"solutionIndex\": 0, \"tags\": [\"fruit\"], \"id\": \"2\" }]," +
                "\"next\": null }",
                "application/json");
		QuizQuery query = new QuizQuery("fruit", "no fromm string... whaaaat?");
		JSONObject result = proxy.retrieveQuizQuestion(query);
		try {
			JSONArray array = result.getJSONArray("questions");
			JSONObject question1 = array.getJSONObject(0);
			assertEquals("q1", question1.get("question"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void testRetrieveQuizQuestionsJSONResponseNULL() {
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.ONLINE);
		mMockClient.pushCannedResponse(
				"POST (?:https?://[^/]+|[^/]+)?/+search\\b",
				AdvancedMockHttpClient.IOEXCEPTION_ERROR_CODE, "", "");
		QuizQuery query = new QuizQuery("fruit", "no fromm string... whaaaat?");
		assertEquals(null, proxy.retrieveQuizQuestion(query));
	}
	
	public void testRetrieveQuizQuestionsJSONArrayNULL(){
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.ONLINE);
		mMockClient.pushCannedResponse(
				"POST (?:https?://[^/]+|[^/]+)?/+search\\b",
                HttpStatus.SC_OK,
                "{\"questions\": null ," +
                "\"next\": null }",
                "application/json");
		QuizQuery query = new QuizQuery("fruit", "no fromm string... whaaaat?");
		assertEquals(null, proxy.retrieveQuizQuestion(query));
	}
}
