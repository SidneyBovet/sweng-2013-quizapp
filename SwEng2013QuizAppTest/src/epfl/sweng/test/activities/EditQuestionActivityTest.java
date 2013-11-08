package epfl.sweng.test.activities;

import java.util.concurrent.Semaphore;

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
		getSolo().sleep(100);
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
		getSolo().sleep(100);
		assertFalse("Button is disabled", getSolo().getButton("Submit")
				.isEnabled());

	}

	public void testOnlyOneAnswerAtBeggining() throws InterruptedException {
		getSolo().sleep(100);
		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);
		int a = lw.getAdapter().getCount();
		assertEquals(1, a);
	}

	public void testAddAnswerWhenButtonPlusIsClicked() {
		getSolo().sleep(500);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);

		assertEquals(2, lw.getAdapter().getCount());

	}

	public void testHave3answerwhenPlusButtonIsClicked2times() {
		getSolo().sleep(500);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);

		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);
		assertEquals(3, lw.getAdapter().getCount());
	}

	public void testCanRemoveFirstAnswerRow() {
		getSolo().sleep(100);
		getSolo().clickOnButton("-");
		waitFor(TTChecks.QUESTION_EDITED);

		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);
		assertEquals(0, lw.getAdapter().getCount());

	}

	public void testAnswerButtonChangeWhenClickedOn() {
		getSolo().sleep(100);
		assertTrue("Answer must be false",
				getSolo().searchButton("" + (char) 10008));
		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		assertTrue("Answer icone must be true",
				getSolo().searchButton("" + (char) 10004));

	}

	public void testCanNotSubmitIfQuestionIsMissing() {
		getSolo().sleep(100);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "   ");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag1");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"Answer D");

		getSolo().clickOnButton("" + (char) 10008);
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().sleep(500);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitIfTagsIsMissing() {
		getSolo().sleep(100);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"    ");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"Answer D");

		getSolo().clickOnButton("" + (char) 10008);
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitIfAnswerIsMissing() {
		getSolo().sleep(100);
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
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitIfNotAnswerIsIndicated() {
		getSolo().sleep(500);
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
		getSolo().sleep(100);
		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("\\+");
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
		getSolo().sleep(500);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "ma question");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"   ");
		getSolo().clickOnButton("" + (char) 10008);
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Enabled",
				getSolo().getButton("Submit").isEnabled());
	}

	public void testCanSubmitWhenEveryThingIsCorrectlyField() {
		getSolo().sleep(100);
		getSolo().clickOnButton("\\+");
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
		getSolo().sleep(500);
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
		getSolo().sleep(500);
		assertTrue("Submit button must be Enabled",
				getSolo().getButton("Submit").isEnabled());
		getSolo().clickOnButton("-");
		waitFor(TTChecks.QUESTION_EDITED);
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());
	}

	public void testSubmitButtonIsDisabledWhenEverthingWasOkButWeAddANewAnswer() {
		getSolo().sleep(100);
		getSolo().clickOnButton("\\+");
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
		getSolo().sleep(500);
		getSolo().clickOnButton("\\+");
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
		
		final Semaphore semaphore = new Semaphore(0);
		
		getSolo().sleep(500);
		assertTrue( "Audit fresh Activity is not zero",getActivity().auditErrors() == 0);
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				EditQuestionActivity activity = (EditQuestionActivity) getActivity();
				Button button = (Button) activity.findViewById(R.id.submit_question_button);
				button.setEnabled(true);
				semaphore.release();
			}
		});
		
		try {
			semaphore.acquire();
			getSolo().sleep(500);
			assertTrue("# Audit errors = " + getActivity().auditErrors() + " != 5",
					getActivity().auditErrors() == 5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer D");
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question1");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");
		
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer BBBBBB");	
		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		
		assertTrue("Number of audit errors = " + getActivity().auditErrors() + " != 0",
				getActivity().auditErrors() == 0);
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				EditQuestionActivity activity = (EditQuestionActivity) getActivity();
				Button button = (Button) activity.findViewById(R.id.submit_question_button);
				button.setEnabled(false);
				semaphore.release();
			}
		});
		
		try {
			semaphore.acquire();
			getSolo().sleep(500);
			assertTrue("# Audit errors = " + getActivity().auditErrors() + " != 1",
					getActivity().auditErrors() == 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				EditQuestionActivity activity = (EditQuestionActivity) getActivity();
				Button button = (Button) activity.findViewById(R.id.submit_question_button);
				button.setText("Not submit");
				semaphore.release();
			}
		});
		
		try {
			semaphore.acquire();
			getSolo().sleep(500);
			assertTrue("# Audit errors = " + getActivity().auditErrors() + " != 2",
					getActivity().auditErrors() == 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
