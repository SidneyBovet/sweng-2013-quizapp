package epfl.sweng.test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;
import epfl.sweng.servercomm.QuizEditExecution;

public class QuizEditExecutionTest extends TestCase {
	private static final int ERROR_CODE = 401;
	private ArrayList<String> mAnswers = new ArrayList<String>();
	private ArrayList<String> mTags = new ArrayList<String>();
	private MockJSON mMockJson = null;
	private final int id = -33;
	private final int index = -100;

	public QuizEditExecutionTest() {
	}

	@Override
	protected void setUp() throws Exception {
		mAnswers.add("reponse1");
		mAnswers.add("  ¥    "); // Blank is an error.
		mTags.add("**((/_:_:_");
		mMockJson = new MockJSON(id, "ma question", mAnswers, index, mTags,
				"BOB");

		super.setUp();
	}

	public void testBadRequestWhenBadJSON() {
		QuizEditExecution quizEditExecute = new QuizEditExecution();

		quizEditExecute.execute(mMockJson);
		// HOW TO TEST IF THE RESULT OF THE REQUEST IS BAD ? DO I NEED TO CHANGE
		// THE CODE IN QuizEditExecution ?

		// 401 is the status number of bad request ?
		int status = -1;
		try {
			status = quizEditExecute.get();
		} catch (InterruptedException e) {
			fail("InterruptedException while getting status");
		} catch (ExecutionException e) {
			fail("ExecutionException while getting status");
		}
		assertEquals(ERROR_CODE, status);
	}
}
