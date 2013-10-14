package epfl.sweng.test;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.json.JSONException;

import android.util.Log;

import junit.framework.TestCase;
import epfl.sweng.servercomm.QuizEditExecution;
import epfl.sweng.servercomm.SwengHttpClientFactory;

public class QuizEditExecutionTest extends TestCase {
	private ArrayList<String> mAnswers = new ArrayList<String>();
	private ArrayList<String> mTags = new ArrayList<String>();
	private MockJSON mMockJson = null;
	private int id = -33;
	private int index = -100;

	public QuizEditExecutionTest() {
		mAnswers.add("reponse1");
		mAnswers.add("      "); // Blank is an error.
		mTags.add("**((/_:_:_");
		mMockJson = new MockJSON(id, "ma question", mAnswers, index, mTags,
				"BOB");

	}

	public void testBadRequestWhenBadJSON() {
		QuizEditExecution quizEditExecute = new QuizEditExecution();
		quizEditExecute.execute(mMockJson);
		// HOW TO TEST IF THE RESULT OF THE REQUEST IS BAD ? DO I NEED TO CHANGE
		// THE CODE IN QuizEditExecution ?

		// 401 is the status number of bad request ?
		assertEquals(true, true);
	}
}
