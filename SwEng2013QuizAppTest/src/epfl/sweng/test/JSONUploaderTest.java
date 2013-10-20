package epfl.sweng.test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import epfl.sweng.servercomm.JSONUploader;
<<<<<<< Updated upstream
=======
import epfl.sweng.servercomm.SwengHttpClientFactory;
>>>>>>> Stashed changes
import epfl.sweng.test.minimalmock.MockHttpClient;

public class JSONUploaderTest extends TestCase {
	private static final int ERROR_CODE = 400;
	private ArrayList<String> mAnswers = new ArrayList<String>();
	private ArrayList<String> mTags = new ArrayList<String>();
	private MockJSON mMockJson = null;
<<<<<<< Updated upstream
	private MockHttpClient mMockClient = new MockHttpClient();
=======
	private MockHttpClient mMockClient;

>>>>>>> Stashed changes
	private final int id = -33;
	private final int index = -100;

	public JSONUploaderTest() {
	}

	@Override
	protected void setUp() throws Exception {
<<<<<<< Updated upstream
=======
		mMockClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(mMockClient);
		super.setUp();
	}

	public void testBadRequestIsHandled() {
>>>>>>> Stashed changes
		mAnswers.add("reponse1");
		mAnswers.add("     "); // Blank is an error.
		mTags.add("**((/_:_:_");
		mMockJson = new MockJSON(id, "ma question", mAnswers, index, mTags,
				"BOB");
<<<<<<< Updated upstream
		
		super.setUp();
	}
	
// XXX C'est pas le but de tester si le serveur r�pond 400 ou pas mais si nous 
//		on g�re le 400
	
	public void testBadRequestIsHandled() {

		mMockClient.pushCannedResponse(
				".+", ERROR_CODE, "", "");
		JSONUploader quizEditExecute = new JSONUploader();
		quizEditExecute.execute(mMockJson);
		try {
			quizEditExecute.get();
=======
		mMockClient.pushCannedResponse(".+", ERROR_CODE, "", "HttpResponse");
		try {
			JSONUploader quizEditExecute = new JSONUploader();
			quizEditExecute.execute(mMockJson);
			quizEditExecute.get();
		} catch (InterruptedException e) {
			fail("What a Terrible Failure (aka. WTF!?");
		} catch (ExecutionException e) {
			fail("An exception was thrown while executing JSONUploader");
		}
	}

	public void testSadPathisOK() {
		mAnswers.add("reponse1");
		mAnswers.add("     "); // Blank is an error.
		mTags.add("**((/_:_:_");
		mMockJson = new MockJSON(id, "ma question", mAnswers, index, mTags,
				"BOB");

		mMockClient.pushCannedResponse(".+", ERROR_CODE, "", "HttpResponse");
		JSONUploader quizEditExecute = new JSONUploader();
		quizEditExecute.execute(mMockJson);
		int result = -1;
		try {
			result = quizEditExecute.get();
		} catch (InterruptedException e) {
			fail("What a Terrible Failure (aka. WTF!?");
		} catch (ExecutionException e) {
			fail("An exception was thrown while executing JSONUploader");
		}
		assertTrue("Must be 400 since it's a BAD result", result == 400);
	}

	public void testGoodPathisOK() {
		mAnswers.add("reponse1");
		mAnswers.add("reposne2"); // Blank is an error.
		mTags.add("tag1");
		mMockJson = new MockJSON(1, "ma question", mAnswers, 1, mTags, "BOB");
		mMockClient.pushCannedResponse(".+", 200, "", "HttpResponse");
		JSONUploader quizEditExecute = new JSONUploader();
		quizEditExecute.execute(mMockJson);
		int result = -1;
		try {
			result = quizEditExecute.get();
>>>>>>> Stashed changes
		} catch (InterruptedException e) {
			fail("What a Terrible Failure (aka. WTF!?");
		} catch (ExecutionException e) {
			fail("An exception was thrown while executing JSONUploader");
		}
<<<<<<< Updated upstream
=======
		assertTrue("Must be 200 since it's a OK result", result == 200);

>>>>>>> Stashed changes
	}
}
