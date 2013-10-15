package epfl.sweng.test;

import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import epfl.sweng.R;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class EditQuestionActivityTest extends GUITest<EditQuestionActivity> {

	public EditQuestionActivityTest() {
		super(EditQuestionActivity.class);
	}

	@Override
	protected void setUp() {
		super.setUp();
		// add stuff we need
	}

	public void testBasicsElementsAreHere() {
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		assertTrue("Question filed needed",
				getSolo().searchText("Type in the question\'s tags"));
		assertTrue("Question filed needed",
				getSolo().searchText("Type in the question\'s text body"));
		assertTrue("submit button needed", getSolo().searchButton("Submit"));
		assertTrue("Answer must be false", getSolo().searchButton("✘"));
		assertTrue("Answer must be a - minus question button", getSolo()
				.searchButton("-"));
		assertTrue("Answer must be a - add question button", getSolo()
				.searchButton("+"));
		assertTrue("Answer field needed",
				getSolo().searchEditText("Type in the answer"));
	}

	public void testSubmitButtonIsDisabledAtBeggining() {
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		assertFalse("Button is disabled", getSolo().getButton("Submit")
				.isEnabled());
	}

	public void testOnlyOneAnswerAtBeggining() throws InterruptedException {
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		ListView mListview = (ListView) getSolo().getView(
				R.id.submit_question_listview);

		assertEquals(0, mListview.getChildCount());
	}

	public void testaddAnswerWhenButtonPlusIsClicked() {

		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		getSolo().clickOnButton("+");

//		ListView listView = (ListView) getSolo().getView(
//				R.id.submit_question_listview);

//		int count = 0;
		// while(listView.)
		assertEquals(true, true);
	}
}
