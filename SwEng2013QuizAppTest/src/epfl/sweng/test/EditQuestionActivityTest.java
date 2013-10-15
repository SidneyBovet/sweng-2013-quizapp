package epfl.sweng.test;

import android.view.View;
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

	public void testOnlyOneAnswerAtBeggining() {
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		View mainList = getSolo().getCurrentViews().get(0);

		int a = vMainList.getChildCount();
		System.out.println("KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK  => "
				+ a);
		// ArrayList<TextView> vTiles = new ArrayList<TextView>();
		// vTiles = solo.getCurrentTextViews(vMainList);
		// Log.i("Total number of texts into list are ", vTiles.size());
		// for (int i = 0; i < vTiles.size(); i++)
		// Log.i(" ", vTiles.get(i).getText().toString());
		assertEquals(true, true);
	}
}
