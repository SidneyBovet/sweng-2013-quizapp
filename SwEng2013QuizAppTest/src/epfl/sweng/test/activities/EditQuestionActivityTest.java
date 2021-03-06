package epfl.sweng.test.activities;

import java.util.List;

import org.apache.http.HttpStatus;

import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import epfl.sweng.R;
import epfl.sweng.editquestions.AnswerListAdapter;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.AdvancedMockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class EditQuestionActivityTest extends GUITest<EditQuestionActivity> {

	public EditQuestionActivityTest() {
		super(EditQuestionActivity.class);
	}

	@Override
	protected void setUp() {
		super.setUp();
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		getSolo().sleep(750);
		// add stuff we need
	}
	
	@UiThreadTest
	public void testAnswerListAdapter() {
		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);
		AnswerListAdapter adapter = (AnswerListAdapter) lw.getAdapter();
		
		adapter.add("Test");
		List<String> answers = adapter.getAnswerList();
		assertEquals(answers.get(1), "Test");
		int correct = adapter.getCorrectIndex();
		assertEquals(correct, -1);
		String answer = (String) adapter.getItem(0);
		assertEquals(answer, "");
		long id = adapter.getItemId(0);
		assertEquals(id, 0);
		int errors = adapter.auditErrors();
		assertEquals(errors, 2);
		
		adapter.notifyDataSetChanged();
		adapter.resetAnswerList();
	}
	
	public void testSubmitQuestion() {
		AdvancedMockHttpClient client = new AdvancedMockHttpClient();
		client.pushCannedResponse("POST https://sweng-quiz.appspot.com",
				HttpStatus.SC_CREATED, "", "application/json");
		SwengHttpClientFactory.setInstance(client);
		
		testCanSubmitWhenEveryThingIsCorrectlyField();
		
		getSolo().clickOnButton("Submit");
		
		getActivityAndWaitFor(TTChecks.NEW_QUESTION_SUBMITTED);
		
		testBasicsElementsAreHere();
	}

	public void testBasicsElementsAreHere() {
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
		assertFalse("Button is disabled", getSolo().getButton("Submit")
				.isEnabled());

	}

	public void testOnlyOneAnswerAtBeggining() throws InterruptedException {
		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);
		int a = lw.getAdapter().getCount();
		assertEquals(1, a);
	}

	public void testAddAnswerWhenButtonPlusIsClicked() {
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);

		assertEquals(2, lw.getAdapter().getCount());

	}

	public void testHave3answerwhenPlusButtonIsClicked2times() {
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("+");
		waitFor(TTChecks.QUESTION_EDITED);

		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);
		assertEquals(3, lw.getAdapter().getCount());
	}

	public void testCanRemoveFirstAnswerRow() {
		getSolo().clickOnButton("-");
		waitFor(TTChecks.QUESTION_EDITED);

		ListView lw = (ListView) getSolo().getView(
				R.id.submit_question_listview);
		assertEquals(0, lw.getAdapter().getCount());

	}

	public void testAnswerButtonChangeWhenClickedOn() {
		assertTrue("Answer must be false",
				getSolo().searchButton("" + (char) 10008));
		getSolo().clickOnButton("" + (char) 10008);
		waitFor(TTChecks.QUESTION_EDITED);
		assertTrue("Answer icone must be true",
				getSolo().searchButton("" + (char) 10004));

	}

	public void testCanNotSubmitIfQuestionIsMissing() {
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
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitIfTagsIsMissing() {
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
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), "my question");

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"   ");
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitIfNotAnswerIsIndicatedWhenMulitpleAnswers() {
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
		assertFalse("Submit button must be Disabled",
				getSolo().getButton("Submit").isEnabled());

	}

	public void testCanNotSubmitWhenEveryThingIsCorrectlyFieldButOnlyOneAnswer() {
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
	
}
