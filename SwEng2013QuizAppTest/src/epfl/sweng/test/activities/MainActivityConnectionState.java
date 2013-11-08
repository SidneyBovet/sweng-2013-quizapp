package epfl.sweng.test.activities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.widget.CheckBox;
import epfl.sweng.R;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.patterns.ConnectivityState;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class MainActivityConnectionState extends GUITest<MainActivity> {
	private Context contextOfMainActivity;
	private UserPreferences persistentStorage;
	
	public MainActivityConnectionState() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() {
		super.setUp();
		contextOfMainActivity = getInstrumentation()
				.getTargetContext();
		persistentStorage = UserPreferences.
				getInstance(contextOfMainActivity);
		persistentStorage.setSessionId("blabla");
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
	}
	
	@Override
	protected void tearDown() throws Exception {
		persistentStorage.destroyAuthentication();
		persistentStorage.setConnectivityState(ConnectivityState.ONLINE);
		super.tearDown();
	};
	
	public void testBeginConnected() {
		assertTrue(persistentStorage.isConnected());
	}
	
	public void testCheckBoxCheckDisconnected() {
		CheckBox connexionState = (CheckBox) getSolo().getView(
				R.id.switchOnlineModeCheckbox);;
		getSolo().clickOnView(connexionState);
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		assertFalse(persistentStorage.isConnected());
	}
	
	public void testCheckBoxCheckConnected() {
		CheckBox connexionState = (CheckBox) getSolo().getView(
				R.id.switchOnlineModeCheckbox);;
		getSolo().clickOnView(connexionState);
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		getSolo().sleep(2000);
		getSolo().clickOnView(connexionState);
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_DISABLED);
		getSolo().sleep(2000);
		assertTrue(persistentStorage.isConnected());
	}
	
	public void testSendingOrderIsFIFO() {
		CheckBox connexionState = (CheckBox) getSolo().getView(
				R.id.switchOnlineModeCheckbox);
				
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		
		QuestionsProxy.getInstance().addOutbox(createFakeQuestion(
				"Statement 1"));
		QuestionsProxy.getInstance().addOutbox(createFakeQuestion(
				"Statement 2"));
		
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
	}
	
	public void testUncheckingBoxEmptiesOutbox() {
		CheckBox connexionState = (CheckBox) getSolo().getView(
				R.id.switchOnlineModeCheckbox);

		getSolo().clickOnView(connexionState);
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		getSolo().sleep(2000);

		QuestionsProxy.getInstance().addOutbox(createFakeQuestion(
				"How reliable Robotium testing is?"));
		
		getSolo().clickOnView(connexionState);
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_DISABLED);
		getSolo().sleep(2000);
		getActivityAndWaitFor(TTChecks.NEW_QUESTION_SUBMITTED);
		
		assertEquals("Outbox should be empty after going from offline to online",
				0, QuestionsProxy.getInstance().getOutboxSize());
	}

	private QuizQuestion createFakeQuestion(String questionStatement) {
		List<String> answers = new ArrayList<String>();
		answers.add("100% accurate");
		answers.add("Fully voodoo and could generate non-pseudorandom numbers");

		Set<String> tags = new HashSet<String>();
		tags.add("robotium");
		tags.add("testing");
		
		QuizQuestion question = new QuizQuestion(
				questionStatement, answers, 1, tags);
		return question;
	}
}
