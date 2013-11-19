package epfl.sweng.test.activities;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
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
		getSolo().sleep(1000);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test Cases
	 */

	public void testBasicElementsPresent() {
		String queryDefaultText = context
				.getString(R.string.search_question_query);
		String buttonDefaultText = context
				.getString(R.string.SearchQueryButton);
		assertTrue("Query EditText could not be found.", getSolo().searchText(queryDefaultText));
		assertTrue("Query Button could not be found.", getSolo().searchButton(buttonDefaultText));
	}
	
	public void testSearchButtonDisabledAtStart() {
		String buttonDefaultText = context
				.getString(R.string.SearchQueryButton);
		Button queryButton = getSolo().getButton(buttonDefaultText);
		assertTrue("Search Button is not disabled at start.", !queryButton.isEnabled());
	}
	
	public void testSearchButtonCorrectQuerySingleTag() {
		fillQueryAndTestButton("banana", true);
	}
	
	public void testSearchButtonCorrectQueryMultipleTags() {
		fillQueryAndTestButton("banana apple orange", true);
	}
	
	public void testSearchButtonCorrectQueryPlusOperator() {
		fillQueryAndTestButton("(strawberry + raspberry)", true);
	}
	
	public void testSearchButtonCorrectQueryMultiplePlusOperator() {
		fillQueryAndTestButton("(strawberry + raspberry + blackberry)", true);
	}
	
	public void testSearchButtonCorrectQueryTagWithPlusOperator() {
		fillQueryAndTestButton("(strawberry + raspberry) banana", true);
	}
	
	public void testSearchButtonCorrectQueryMultipleTagsWithPlusOperator() {
		fillQueryAndTestButton("banana (stawberry + raspberry) apple", true);
	}
	
	public void testSearchButtonCorrectQueryMultipleTagsWithMultiplePlusOperator() {
		fillQueryAndTestButton("banana (strawberry + raspberry + blackberry) apple orange", true);
	}
	
	public void testSearchButtonIncorrectQueryOnlyWhitespaces() {
		fillQueryAndTestButton("  \t n   \n\t ", false);
	}
	
	public void testSearchButtonIncorrectQueryBadOpenParenthesis() {
		fillQueryAndTestButton("(strawberry", false);
	}
	
	public void testSearchButtonIncorrectQueryBadClosedParenthesis2() {
		fillQueryAndTestButton("raspberry)", false);
	}
	
	public void testSearchButtonIncorrectQueryBadPlusOperator() {
		fillQueryAndTestButton("(strawberry raspberry +)", false);
	}
	
	public void testSearchButtonIncorrectQueryBadLetters() {
		fillQueryAndTestButton("*ç§¦$|¢9#", false);
	}

	private void fillQueryAndTestButton(String query, boolean enabled) {
		String buttonDefaultText = context
				.getString(R.string.SearchQueryButton);
		Button queryButton = getSolo().getButton(buttonDefaultText);
		
		String queryDefaultText = context
				.getString(R.string.search_question_query);
		EditText queryText = getSolo().getEditText(queryDefaultText);
		
		getSolo().enterText(queryText, query);
		assertTrue("Search Button is not " + (enabled ? "enabled":"disabled")
				+ " if query = \"" + query + "\".",
				queryButton.isEnabled() == enabled);
	}
}
