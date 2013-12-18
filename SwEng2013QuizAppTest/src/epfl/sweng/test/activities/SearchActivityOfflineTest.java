package epfl.sweng.test.activities;

import java.util.Arrays;
import java.util.HashSet;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.comm.ConnectivityState;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.SearchActivity;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class SearchActivityOfflineTest extends GUITest<SearchActivity> {

	private QuizQuestion mQuestion1;
	private QuizQuestion mQuestion2;
	private CacheContentProvider mContentProvider;
	private Context mContext;
	
	public SearchActivityOfflineTest() {
		super(SearchActivity.class);
	}
	
	@Override
	public void setUp() {
		super.setUp();
		getActivityAndWaitFor(TTChecks.SEARCH_ACTIVITY_SHOWN);
		getSolo().sleep(1000);
		mQuestion1 = new QuizQuestion("Hai guise", Arrays.asList("Hello", "Sup", "Howdy"),
				1, new HashSet<String>(Arrays.asList("hello", "test")), 1, "me");
		mQuestion2 = new QuizQuestion("Don't worry dear pamela", Arrays.asList(
				"I'll do my scientific best", "To control your fleet"), 0,
				new HashSet<String>(Arrays.asList("hello", "test")), 2, "her");
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.OFFLINE);
		mContentProvider = new CacheContentProvider(true);
		mContentProvider.addQuizQuestion(mQuestion1);
		mContentProvider.addQuizQuestion(mQuestion2);
		mContext = getInstrumentation().getTargetContext();
	}
	
	@Override
	public void tearDown() throws Exception {
		mContentProvider.eraseDatabase();
		mContentProvider.close();
		super.tearDown();
	}
	
	public void testSimpleQuery() {
		fillQueryAndClickButton("test");
		
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		
		assertTrue(getSolo().searchText("Hai guise"));
	}
	
	public void testNotWorkingQuery() {
		fillQueryAndClickButton("banana");
		
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		
		assertTrue(getSolo().searchText("error"));
	}
	
	public void testMultipleQuery() {
		fillQueryAndClickButton("test + banana");
		
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		
		assertTrue(getSolo().searchText("Hai guise"));
	}
	
	public void testMultipleQueryInverted() {
		fillQueryAndClickButton("banana + test");
		
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		
		assertTrue(getSolo().searchText("Hai guise"));
	}
	
	private void fillQueryAndClickButton(String query) {
		getSolo().sleep(500);
		String buttonDefaultText = mContext
				.getString(R.string.search_query_button);
		Button queryButton = getSolo().getButton(buttonDefaultText);

		String queryDefaultText = mContext
				.getString(R.string.search_question_query);
		EditText queryText = getSolo().getEditText(queryDefaultText);

		getSolo().enterText(queryText, query);
		getSolo().sleep(500);
		assertTrue("Query button must be enabled", queryButton.isEnabled());
		getSolo().clickOnButton(buttonDefaultText);
	}
}
