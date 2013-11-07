package epfl.sweng.test.activities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpStatus;

import android.widget.EditText;
import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.patterns.ConnectivityState;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.quizquestions.QuizQuestion;
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
			UserPreferences.getInstance(getInstrumentation().getTargetContext()).
				setConnectivityState(ConnectivityState.ONLINE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testSubmittedQuestionIsInProxyOutbox() {
		UserPreferences.getInstance(getInstrumentation().getTargetContext()).
			setConnectivityState(ConnectivityState.OFFLINE);
		
		SwengHttpClientFactory.setInstance(mUnconnectedClient);
		
		int expectedOutboxSize = QuestionsProxy.getInstance().getOutboxSize() + 1;
		
		fillFormWithCorrectQuestion();
		getSolo().clickOnButton("Submit");
		getActivityAndWaitFor(TTChecks.NEW_QUESTION_SUBMITTED);
		
		assertEquals(expectedOutboxSize, QuestionsProxy.getInstance().getOutboxSize());
	}

	public void testOutboxIsCompletelySentAfterSuccessfulConnection() {

		UserPreferences.getInstance(getInstrumentation().getTargetContext()).
			setConnectivityState(ConnectivityState.ONLINE);
		
		SwengHttpClientFactory.setInstance(mMockClient);
		mMockClient.pushCannedResponse("", HttpStatus.SC_CREATED, "", "");
		
		QuestionsProxy.getInstance().addOutbox(createFakeQuestion());
		
		fillFormWithCorrectQuestion();

		getSolo().clickOnButton("Submit");
		getActivityAndWaitFor(TTChecks.NEW_QUESTION_SUBMITTED);
		
		assertEquals(0, QuestionsProxy.getInstance().getOutboxSize());
	}

	private QuizQuestion createFakeQuestion() {
		List<String> answers = new ArrayList<String>();
		answers.add("100% accurate");
		answers.add("Fully voodoo and could generate non-pseudorandom numbers");

		Set<String> tags = new HashSet<String>();
		tags.add("robotium");
		tags.add("testing");
		
		QuizQuestion question = new QuizQuestion(
				"How reliable Robotium testing is?", answers, 1, tags);
		return question;
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
