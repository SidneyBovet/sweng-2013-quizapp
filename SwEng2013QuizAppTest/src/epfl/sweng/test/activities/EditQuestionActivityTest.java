package epfl.sweng.test.activities;

import java.util.concurrent.Semaphore;

import android.view.View;
import android.widget.Button;
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
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		// add stuff we need
	}

	public void testBasicsElementsAreHere() {
		getSolo().sleep(1);
		assertTrue("Tag field needed",
				getSolo().searchText("Type in the question\'s tags"));
		assertTrue("Question field needed",
				getSolo().searchText("Type in the answer"));
		assertTrue("Answer field needed",
				getSolo().searchText("Type in the question\'s text body"));
		assertTrue("submit button needed", getSolo().searchButton("Submit"));
		assertTrue("Answer must be false",
				getSolo().searchButton("" + (char) 10008));
		assertTrue("Answer must be a - minus question button", getSolo()
				.searchButton("-"));
		assertTrue("Answer must be a - add question button", getSolo()
				.searchButton("+"));
		assertTrue("Answer field needed",
				getSolo().searchEditText("Type in the answer"));

	}

	public void testSubmitButtonIsDisabledAtBeggining() {
		getSolo().sleep(1);
		assertFalse("Button is disabled", getSolo().getButton("Submit")
				.isEnabled());

	}

	public void testOnlyOneAnswerAtBeggining() throws InterruptedException {
		getSolo().sleep(1);
		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);
		int a = lw.getAdapter().getCount();
		assertEquals(1, a);
	}

	public void testAddAnswerWhenButtonPlusIsClicked() {
		getSolo().sleep(1);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);

		assertEquals(2, lw.getAdapter().getCount());

	}

	public void testHave3answerwhenPlusButtonIsClicked2times() {
		getSolo().sleep(1);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);

		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);
		assertEquals(3, lw.getAdapter().getCount());
	}

	public void testCanRemoveFirstAnswerRow() {
		getSolo().sleep(1);
		getSolo().clickOnButton("-");
		waitFor(TTChecks.QUESTION_EDITED);

		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);
		assertEquals(0, lw.getAdapter().getCount());

	}

	public void testAnswerButtonChangeWhenClickedOn() {
		getSolo().sleep(1);
		assertTrue("Answer must be false",
				getSolo().searchButton("" + (char) 10008));
		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		assertTrue("Answer icone must be true",
				getSolo().searchButton("" + (char) 10004));

	}

	public void testCanNotSubmitIfQuestionIsMissing() {
		getSolo().sleep(1);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "   ");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag1");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"Answer D");

		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitIfTagsIsMissing() {
		getSolo().sleep(1);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"    ");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"Answer D");

		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitIfAnswerIsMissing() {
		getSolo().sleep(1);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"   ");

		getSolo().clickOnButton("" + (char) 10008);
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		//waitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitIfNotAnswerIsIndicated() {
		getSolo().sleep(1);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"   ");
		getSolo().sleep(200);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitIfNotAnswerIsIndicatedWhenMulitpleAnswers() {
		getSolo().sleep(1);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"   ");
		getSolo().sleep(200);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitWhenEveryThingIsCorrectlyFieldButOnlyOneAnswer() {
		getSolo().sleep(1);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "ma question");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"   ");
		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Enabled",
				getSolo().getButton("Submit").isEnabled());
	}

	public void testCanSubmitWhenEveryThingIsCorrectlyField() {
		getSolo().sleep(1);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question1");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer D");
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer BBBBBB");
		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		assertTrue("Submit button must be Enabled",
				getSolo().getButton("Submit").isEnabled());
	}

	public void testWhenRemoveAnswerSubmitIsDisabled() {
		getSolo().sleep(1);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question1");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer D");
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer BBBBBB");
		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		assertTrue("Submit button must be Enabled",
				getSolo().getButton("Submit").isEnabled());
		getSolo().clickOnButton("-");
		waitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());
	}

	public void testSubmitButtonIsDisabledWhenEverthingWasOkButWeAddANewAnswer() {
		getSolo().sleep(1);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question1");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer AAAAAA");
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer BBBBBB");
		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		assertTrue("Submit button must be Enabled",
				getSolo().getButton("Submit").isEnabled());
		getSolo().clickOnButton("-");
		waitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer CCCCCC");
		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		assertTrue("Submit button must be Enabled",
				getSolo().getButton("Submit").isEnabled());
	}

	public void testRemoveSpecificRow() {
		getSolo().sleep(1);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question1");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer AAAAAA");
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer BBBBBB");
		Button butAnswerIndexRow2 = getSolo().getView(Button.class, 3);
		getSolo().clickOnView(butAnswerIndexRow2);
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().sleep(2000);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer CCCCCC");
		// ArrayList<Button> lB = getSolo().getView();
		getSolo().sleep(2000);
		Button butRemoveRow2 = getSolo().getView(Button.class, 4);
		getSolo().clickOnView(butRemoveRow2);
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().sleep(2000);

		assertFalse("Must not be text BBBBBB", getSolo().searchText("answer BBBBBB"));
	}
	
	public void testAudit() {
		final Semaphore s = new Semaphore(1);
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				EditQuestionActivity activity = (EditQuestionActivity) getActivity();
				Button button = (Button) activity.findViewById(R.id.submit_question_button);
				button.setText("Not submit");
				s.release();
			}
		});
		
		try {
			s.acquire();
			System.out.println(getActivity().auditErrors());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
