//package epfl.sweng.test;
//
//import java.util.ArrayList;
//import java.util.concurrent.ExecutionException;
//
//import junit.framework.TestCase;
//import epfl.sweng.servercomm.JSONUploader;
//import epfl.sweng.servercomm.SwengHttpClientFactory;
//import epfl.sweng.test.minimalmock.MockHttpClient;
//
//public class JSONUploaderTest extends TestCase {
//	private static final int ERROR_CODE = 400;
//	private ArrayList<String> mAnswers = new ArrayList<String>();
//	private ArrayList<String> mTags = new ArrayList<String>();
//	private MockJSON mMockJson = null;
//	private MockHttpClient mMockClient;
//
//	private final int id = -33;
//	private final int index = -100;
//
//	public JSONUploaderTest() {
//	}
//
//	@Override
//	protected void setUp() throws Exception {
//		mMockClient = new MockHttpClient();
//		SwengHttpClientFactory.setInstance(mMockClient);
//		super.setUp();
//	}
//
//	public void testBadRequestIsHandled() {
//		mAnswers.add("reponse1");
//		mAnswers.add("     "); // Blank is an error.
//		mTags.add("**((/_:_:_");
//		mMockJson = new MockJSON(id, "ma question", mAnswers, index, mTags,
//				"BOB");
//		mMockClient.pushCannedResponse(".+", ERROR_CODE, "", "HttpResponse");
//		try {
//			JSONUploader quizEditExecute = new JSONUploader();
//			quizEditExecute.execute(mMockJson);
//			quizEditExecute.get();
//		} catch (InterruptedException e) {
//			fail("What a Terrible Failure (aka. WTF!?");
//		} catch (ExecutionException e) {
//			fail("An exception was thrown while executing JSONUploader");
//		}
//	}
//
//	public void testSadPathisOK() {
//		mAnswers.add("reponse1");
//		mAnswers.add("     "); // Blank is an error.
//		mTags.add("**((/_:_:_");
//		mMockJson = new MockJSON(id, "ma question", mAnswers, index, mTags,
//				"BOB");
//
//		mMockClient.pushCannedResponse(".+", ERROR_CODE, "", "HttpResponse");
//		JSONUploader quizEditExecute = new JSONUploader();
//		quizEditExecute.execute(mMockJson);
//		int result = -1;
//		try {
//			result = quizEditExecute.get();
//		} catch (InterruptedException e) {
//			fail("What a Terrible Failure (aka. WTF!?");
//		} catch (ExecutionException e) {
//			fail("An exception was thrown while executing JSONUploader");
//		}
//		assertEquals(400, result);
//	}
//
//	public void testGoodPathisOK() {
//		mAnswers.add("reponse1");
//		mAnswers.add("reposne2"); // Blank is an error.
//		mTags.add("tag1");
//		mMockJson = new MockJSON(1, "ma question", mAnswers, 1, mTags, "BOB");
//		mMockClient.pushCannedResponse(".+", 200, "", "HttpResponse");
//		JSONUploader quizEditExecute = new JSONUploader();
//		quizEditExecute.execute(mMockJson);
//		int result = -1;
//		try {
//			result = quizEditExecute.get();
//		} catch (InterruptedException e) {
//			fail("What a Terrible Failure (aka. WTF!?");
//		} catch (ExecutionException e) {
//			fail("An exception was thrown while executing JSONUploader");
//		}
//		assertEquals(200, result);
//
//	}
//}
