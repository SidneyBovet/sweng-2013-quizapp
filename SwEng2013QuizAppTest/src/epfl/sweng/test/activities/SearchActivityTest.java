package epfl.sweng.test.activities;

import android.content.Context;
import epfl.sweng.R;
import epfl.sweng.searchquestions.SearchActivity;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class SearchActivityTest extends GUITest<SearchActivity> {

	private Context context;

	public SearchActivityTest() {
		super(SearchActivity.class);
	}

	@Override
	public void setUp() {
		super.setUp();
		context = getInstrumentation().getTargetContext();
		getActivityAndWaitFor(TTChecks.SEARCH_ACTIVITY_SHOWN);
		getSolo().sleep(500);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test Cases
	 */

	public void testBasicElementsPresent() {
		String defaultQueryText = context
				.getString(R.string.search_question_query);
		String defaultButtonText = context
				.getString(R.string.SearchQueryButton);
		assertTrue("Query EditText must be present", getSolo().searchText(defaultQueryText));
		assertTrue("Query Button must be present", getSolo().searchButton(defaultButtonText));
	}
}
