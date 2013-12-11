package epfl.sweng.test.activities;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.R;
import epfl.sweng.searchquestions.SearchActivity;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.AdvancedMockHttpClient;
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

	protected void tearDown() throws Exception {
		super.tearDown();
		SwengHttpClientFactory.setInstance(null);
	}

	/*
	 * Test Cases
	 */

	public void testBasicElementsPresent() {
		String queryDefaultText = context
				.getString(R.string.search_question_query);
		String buttonDefaultText = context
				.getString(R.string.search_query_button);
		assertTrue("Query EditText could not be found.",
				getSolo().searchText(queryDefaultText));
		assertTrue("Query Button could not be found.",
				getSolo().searchButton(buttonDefaultText));
	}

	public void testSearchButtonDisabledAtStart() {
		String buttonDefaultText = context
				.getString(R.string.search_query_button);
		Button queryButton = getSolo().getButton(buttonDefaultText);
		assertTrue("Search Button is not disabled at start.",
				!queryButton.isEnabled());
	}

	public void testSearchButtonCorrectQuerySingleTag() {
		fillQueryAndTestButton("banana", true);
	}

	public void testSearchButtonCorrectQueryMultipleTags() {
		fillQueryAndTestButton("banana apple orange", true);
	}

	public void testSearchButtonCorrectParentheses() {
		fillQueryAndTestButton("(banana apple orange)", true);
	}

	public void testSearchButtonCorrectQueryPlusOperator() {
		fillQueryAndTestButton("strawberry + raspberry", true);
	}

	public void testSearchButtonCorrectQueryTagWithPlusOperator() {
		fillQueryAndTestButton("(strawberry + raspberry) banana", true);
	}

	public void testSearchButtonCorrectQueryMultipleTagsWithPlusOperator() {
		fillQueryAndTestButton("banana (stawberry + raspberry) apple", true);
	}

	public void testSearchButtonCorrectQueryStarOperator() {
		fillQueryAndTestButton("strawberry * raspberry", true);
	}

	public void testSearchButtonCorrectQueryTagWithStarOperator() {
		fillQueryAndTestButton("(strawberry * raspberry) banana", true);
	}

	public void testSearchButtonCorrectQueryMultipleTagsWithStarOperator() {
		fillQueryAndTestButton("banana (strawberry * raspberry) apple", true);
	}

	public void testSearchButtonCorrectQueryCombinationPlusAndStar() {
		fillQueryAndTestButton("(strawberry + raspberry) * banana", true);
	}

	public void testSearchButtonCorrectQueryCombinationStarAndPlus() {
		fillQueryAndTestButton("(strawberry * raspberry) + banana", true);
	}

	public void testSearchButtonCorrectQueryCombinationTagWithStarAndPlus() {
		fillQueryAndTestButton("banana (strawberry * raspberry) + apple", true);
	}

	public void testSearchButtonCorrectQueryCombinationMultipleTagsWithStarAndPlus() {
		fillQueryAndTestButton(
				"banana (strawberry * raspberry) + apple orange", true);
	}

	public void testSearchButtonIncorrectQueryOnlyWhitespaces() {
		fillQueryAndTestButton("  \t\n   \n\t ", false);
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
		fillQueryAndTestButton(")-#", false);
	}

	public void testCreateQuery() {
		AdvancedMockHttpClient mockClient = new AdvancedMockHttpClient();
		SwengHttpClientFactory.setInstance(mockClient);

		mockClient
				.pushCannedResponse(
						"POST (?:https?://[^/]+|[^/]+)?/+search\\b",
						HttpStatus.SC_OK,
						"{"
								+ "\"questions\": ["
								+ "{"
								+ "\"id\": \"7654765\","
								+ "\"owner\": \"fruitninja\","
								+ "\"question\": \"How many calories are in a banana?\","
								+ "\"answers\": [ \"Just enough\", \"Too many\" ],"
								+ "\"solutionIndex\": 0,"
								+ "\"tags\": [ \"fruit\", \"banana\", \"trivia\" ]"
								+ "},"
								+ "],"
								+ "\"next\": \"YG9HB8)H9*-BYb88fdsfsyb(08bfsdybfdsoi4\""
								+ "}", "application/json");

		fillQueryAndTestButton("(strawberry + raspberry) * banana", true);
		getSolo().clickOnButton(context.getString(R.string.search_query_button));
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);

		assertTrue(getSolo().searchText("How many calories are in a banana?"));

		getSolo().goBack();

		getSolo().sleep(500); // TODO can we wait for another TTChecks? Aymeric

		String queryDefaultText = context
				.getString(R.string.search_question_query);
		String buttonDefaultText = context
				.getString(R.string.search_query_button);
		assertTrue("Query EditText could not be found.",
				getSolo().searchText(queryDefaultText));
		assertTrue("Query Button could not be found.",
				getSolo().searchButton(buttonDefaultText));

		SwengHttpClientFactory.setInstance(null);
	}

	public void testSendAndResetQuerySearchField() {
		AdvancedMockHttpClient mockClient = new AdvancedMockHttpClient();
		SwengHttpClientFactory.setInstance(mockClient);

		mockClient.pushCannedResponse(".+", HttpStatus.SC_OK, "{"
				+ "\"questions\": [" + "{" + "\"id\": \"7654765\","
				+ "\"owner\": \"fruitninja\","
				+ "\"question\": \"How many calories are in a banana?\","
				+ "\"answers\": [ \"Just enough\", \"Too many\" ],"
				+ "\"solutionIndex\": 0,"
				+ "\"tags\": [ \"fruit\", \"banana\", \"trivia\" ]" + "},"
				+ "]," + "\"next\": \"YG9HB8)H9*-BYb88fdsfsyb(08bfsdybfdsoi4\""
				+ "}", "application/json");

		String buttonDefaultText = context
				.getString(R.string.search_query_button);

		String queryDefaultText = context
				.getString(R.string.search_question_query);
		EditText queryText = getSolo().getEditText(queryDefaultText);

		getSolo().enterText(queryText, "lapin");
		getSolo().clickOnButton(buttonDefaultText);
		getSolo().sleep(3000);
		getSolo().goBack();
		getSolo().sleep(3000);
		assertTrue("Query EditText must be empty.",
				getSolo().searchText(queryDefaultText));
	}

	private void fillQueryAndTestButton(String query, boolean enabled) {
		getSolo().sleep(500);
		String buttonDefaultText = context
				.getString(R.string.search_query_button);
		Button queryButton = getSolo().getButton(buttonDefaultText);

		String queryDefaultText = context
				.getString(R.string.search_question_query);
		EditText queryText = getSolo().getEditText(queryDefaultText);

		getSolo().enterText(queryText, query);
		getSolo().sleep(1000);
		assertTrue("Search Button is not " + (enabled ? "enabled" : "disabled")
				+ " if query = \"" + query + "\".",
				queryButton.isEnabled() == enabled);
	}
}
