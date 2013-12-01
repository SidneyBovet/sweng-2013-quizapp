//package epfl.sweng.test.agent;
//
//import org.json.JSONException;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.test.AndroidTestCase;
//import android.test.RenamingDelegatingContext;
//import epfl.sweng.agents.CachedQuestionAgent;
//import epfl.sweng.backend.QuizQuery;
//import epfl.sweng.caching.CacheContentProvider;
//import epfl.sweng.caching.SQLiteCacheHelper;
//import epfl.sweng.quizquestions.QuizQuestion;
//
//public class CachedQuestionAgentTest extends AndroidTestCase {
//	private CachedQuestionAgent fakeCachedAgent;
//	private QuizQuery fakeQueryCache;
//	private Context contextShowQuestionActivity;
//	private QuizQuestion mQuestion;
//	private CacheContentProvider mProvider;
//
//	@Override
//	protected void setUp() {
//		contextShowQuestionActivity = new RenamingDelegatingContext(
//				getContext(), "test_");
//		mProvider = new CacheContentProvider(contextShowQuestionActivity, true);
//		fakeQueryCache = new QuizQuery("queryOffline", "from");
//		fakeCachedAgent = new CachedQuestionAgent(fakeQueryCache, 
//				contextShowQuestionActivity);
//		try {
//			mQuestion = new QuizQuestion(
//					"{"
//						+ "\"question\": \"What is the answer to life, the universe, and everything?\","
//						+ " \"answers\": [\"Forty-two\", \"Twenty-seven\"],"
//						+ " \"owner\": \"sweng\","
//						+ " \"solutionIndex\": 0,"
//						+ " \"tags\": [\"h2g2\", \"trivia\"],"
//						+ " \"id\": \"1\""
//					+ "}");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		mProvider.addQuizQuestion(mQuestion);
//
//		Cursor cursor = mProvider.getQuestions(new QuizQuery());
//		int id = cursor.getInt(cursor
//				.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_PK));
//		QuizQuestion question = mProvider.getQuestionFromPK(id);
//
//		assertEquals("Statement should be the same.", "lolilol",
//				question.getStatement());
//		try {
//			super.setUp();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@Override
//	protected void tearDown() throws Exception {
//		super.tearDown();
//		fakeCachedAgent.close();
//		mProvider.close();
//	}
//
//	public void testClose() {
//		fakeCachedAgent.close();
//		assertTrue(fakeCachedAgent.isClosed());
//	}
//	
//
//	public void testGetNextQuestion() {
//		assertEquals(mQuestion, fakeCachedAgent.getNextQuestion());
//	}
//}
