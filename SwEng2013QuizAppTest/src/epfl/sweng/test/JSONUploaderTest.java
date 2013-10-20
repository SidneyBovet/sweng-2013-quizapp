//package epfl.sweng.test;
//
//import java.util.ArrayList;
//import java.util.concurrent.ExecutionException;
//
//import junit.framework.TestCase;
//import epfl.sweng.servercomm.JSONUploader;
//import epfl.sweng.test.minimalmock.MockHttpClient;
//
//public class JSONUploaderTest extends TestCase {
//	private static final int ERROR_CODE = 400;
//	private ArrayList<String> mAnswers = new ArrayList<String>();
//	private ArrayList<String> mTags = new ArrayList<String>();
//	private MockJSON mMockJson = null;
//	private MockHttpClient mMockClient = new MockHttpClient();
//	private final int id = -33;
//	private final int index = -100;
//
//	public JSONUploaderTest() {
//	}
//
//	@Override
//	protected void setUp() throws Exception {
//		mAnswers.add("reponse1");
//		mAnswers.add("     "); // Blank is an error.
//		mTags.add("**((/_:_:_");
//		mMockJson = new MockJSON(id, "ma question", mAnswers, index, mTags,
//				"BOB");
//		
//		super.setUp();
//	}
//	
//// XXX C'est pas le but de tester si le serveur répond 400 ou pas mais si nous 
////		on gère le 400
//	
//	public void testBadRequestIsHandled() {
//
//		mMockClient.pushCannedResponse(
//				".+", ERROR_CODE, "", "");
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
//}
