package epfl.sweng.test.activities;

import android.widget.EditText;
import epfl.sweng.caching.OutboxManager;
import epfl.sweng.comm.ConnectivityState;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.preferences.UserPreferences;
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
		
		/* Reseting both client for security */
		mUnconnectedClient = new UnconnectedHttpClient();
		mMockClient = new MockHttpClient();
		
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
		getSolo().sleep(100);
	}
	
	@Override
	protected void tearDown() {
		try {
			super.tearDown();
			UserPreferences.getInstance().
				setConnectivityState(ConnectivityState.ONLINE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testSubmittedQuestionIsInProxyOutbox() {
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.OFFLINE);
		
		SwengHttpClientFactory.setInstance(mUnconnectedClient);
		
		OutboxManager outbox = new OutboxManager();
		int expectedOutboxSize = outbox.size() + 1;
		
		fillFormWithCorrectQuestion();
		getSolo().clickOnButton("Submit");
		getActivityAndWaitFor(TTChecks.NEW_QUESTION_SUBMITTED);
		
		assertEquals(expectedOutboxSize, outbox.size());
		outbox.close();
	}

	private void fillFormWithCorrectQuestion() {
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
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().sleep(100);
	}
}
