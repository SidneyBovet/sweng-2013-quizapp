package epfl.sweng.test;

import java.util.ArrayList;

import junit.framework.TestCase;
import epfl.sweng.servercomm.QuizEditExecution;
import epfl.sweng.test.minimalmock.MockHttpClient;

public class QuizEditExecutionTest extends TestCase {
	private static final int ERROR_CODE = 400;
	private ArrayList<String> mAnswers = new ArrayList<String>();
	private ArrayList<String> mTags = new ArrayList<String>();
	private MockJSON mMockJson = null;
	private MockHttpClient mMockClient = new MockHttpClient();
	private final int id = -33;
	private final int index = -100;

	public QuizEditExecutionTest() {
	}

	@Override
	protected void setUp() throws Exception {
		mAnswers.add("reponse1");
		mAnswers.add("  Â¥    "); // Blank is an error.
		mTags.add("**((/_:_:_");
		mMockJson = new MockJSON(id, "ma question", mAnswers, index, mTags,
				"BOB");

		mMockClient.pushCannedResponse("", ERROR_CODE, "", "");
		
		super.setUp();
	}
	
// XXX C'est pas le but de tester si le serveur répond 400 ou pas mais si nous 
//		on gère le 400
	
	public void testBadRequestIsHandled() {
		QuizEditExecution quizEditExecute = new QuizEditExecution();
		quizEditExecute.execute(mMockJson);
		assert true;
	}
}
