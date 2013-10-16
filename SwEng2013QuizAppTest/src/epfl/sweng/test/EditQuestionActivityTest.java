package epfl.sweng.test;

import android.widget.EditText;
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
		assertTrue("Tag field needed",
				getSolo().searchText("Type in the question\'s tags"));
		assertTrue("Question field needed",
				getSolo().searchText("Type in the answer"));
		assertTrue("Answer field needed",
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
		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);
		int a = lw.getAdapter().getCount();
		assertEquals(1, a);
	}

	public void testAddAnswerWhenButtonPlusIsClicked() {

		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		getSolo().clickOnButton("+");
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);

		assertEquals(2, lw.getAdapter().getCount());

	}

	public void testHave3answerwhenPlusButtonIsClicked2times() {
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		getSolo().clickOnButton("+");
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("+");
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);

		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);
		assertEquals(3, lw.getAdapter().getCount());

	}

	public void testCanRemoveFirstAnswerRow() {
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		getSolo().clickOnButton("-");
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);

		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);
		assertEquals(0, lw.getAdapter().getCount());

	}

	public void testAnswerButtonChangeWhenClickedOn() {
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		assertTrue("Answer must be false", getSolo().searchButton("✘"));
		getSolo().clickOnButton("✘");
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		assertTrue("Answer icone must be true", getSolo().searchButton("✔"));

	}

	public void testCanNotSubmitIfQuestionIsMissing() {

		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "   ");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag1");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"La reponse D");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("✘");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitIfTagsIsMissing() {

		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "ma question");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"    ");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"La reponse D");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("✘");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitIfAnswerIsMissing() {

		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "ma question");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"   ");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("✘");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitIfNotAnswerIsIndicated() {

		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "ma question");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"   ");

		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitIfNotAnswerIsIndicatedWhenMulitpleAnswers() {

		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		getSolo().clickOnButton("+");
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("+");
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("+");
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "ma question");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");
		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"   ");

		// getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

}
