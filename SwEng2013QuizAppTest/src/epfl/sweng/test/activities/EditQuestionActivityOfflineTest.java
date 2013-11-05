package epfl.sweng.test.activities;

import org.apache.http.HttpStatus;

import android.widget.EditText;
import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.test.minimalmock.UnconnectedHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class EditQuestionActivityOfflineTest extends GUITest<EditQuestionActivity> {

	MockHttpClient mMockClient;
	UnconnectedHttpClient mUnconnectedClient;
	
	public EditQuestionActivityOfflineTest() {
		super(EditQuestionActivity.class);
	}

	@Override
	protected void setUp() {
		super.setUp();
		UserPreferences.getInstance(getInstrumentation().getContext()).
			createEntry("CONNECTION_STATE", "OFFLINE");
		
		/* Reseting both client for security */
		mUnconnectedClient = new UnconnectedHttpClient();
		mMockClient = new MockHttpClient();
		
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		getSolo().sleep(100);
	}

	public void testSubmittedQuestionIsInProxyOutbox() {
		SwengHttpClientFactory.setInstance(mUnconnectedClient);
		
		int expectedOutboxSize = QuestionsProxy.getInstance().getOutboxSize() + 1;
		
		fillFormWithCorrectQuestion();
		getSolo().clickOnButton("Submit");
		getActivityAndWaitFor(TTChecks.NEW_QUESTION_SUBMITTED);
		
		assertEquals(expectedOutboxSize, QuestionsProxy.getInstance().getOutboxSize());
	}

	public void testOutboxIsCompletelySentAfterSuccessfulConnection() {
		SwengHttpClientFactory.setInstance(mMockClient);
		mMockClient.pushCannedResponse("", HttpStatus.SC_CREATED, "", "");
		
		fillFormWithCorrectQuestion();

		getSolo().clickOnButton("Submit");
		getActivityAndWaitFor(TTChecks.NEW_QUESTION_SUBMITTED);
		
		assertEquals(0, QuestionsProxy.getInstance().getOutboxSize());
	}

	private void fillFormWithCorrectQuestion() {
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
		getSolo().sleep(100);
	}
}
