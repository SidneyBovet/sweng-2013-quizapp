package epfl.sweng.test.activities;

import android.widget.EditText;
import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class EditQuestionActivityOfflineTest extends GUITest<EditQuestionActivity> {

	public EditQuestionActivityOfflineTest() {
		super(EditQuestionActivity.class);
	}

	@Override
	protected void setUp() {
		super.setUp();
		UserPreferences.getInstance(getInstrumentation().getContext()).
			createEntry("CONNECTION_STATE", "OFFLINE");
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		getSolo().sleep(100);
	}

	public void testSubmittedQuestionIsInProxyOutbox() {
		int expectedOutboxSize = QuestionsProxy.getInstance().getOutboxSize() + 1;
		getSolo().sleep(100);
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
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().clickOnButton("Submit");
		getActivityAndWaitFor(TTChecks.NEW_QUESTION_SUBMITTED);
		
		assertEquals(expectedOutboxSize, QuestionsProxy.getInstance().getOutboxSize());
	}
}
