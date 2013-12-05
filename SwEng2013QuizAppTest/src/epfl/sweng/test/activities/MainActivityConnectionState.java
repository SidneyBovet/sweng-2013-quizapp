package epfl.sweng.test.activities;

import org.apache.http.HttpStatus;

import android.widget.CheckBox;
import epfl.sweng.R;
import epfl.sweng.comm.ConnectivityState;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class MainActivityConnectionState extends GUITest<MainActivity> {
	private UserPreferences persistentStorage;
	
	public MainActivityConnectionState() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() {
		super.setUp();
		persistentStorage = UserPreferences.getInstance();
		persistentStorage.setSessionId("blabla");
		persistentStorage.setConnectivityState(ConnectivityState.ONLINE);
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
	}
	
	@Override
	protected void tearDown() throws Exception {
		persistentStorage.destroyAuthentication();
		SwengHttpClientFactory.setInstance(null);
		super.tearDown();
	};
	
	public void testBeginConnected() {
		assertTrue(persistentStorage.isConnected());
	}
	
	public void testCheckBoxCheckDisconnected() {
		CheckBox connectivityState = (CheckBox) getSolo().getView(
				R.id.switchOnlineModeCheckbox);;
		getSolo().clickOnView(connectivityState);
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		assertFalse(persistentStorage.isConnected());
	}
	
	public void testCheckBoxCheckConnected() {
		CheckBox connectivityState = (CheckBox) getSolo().getView(
				R.id.switchOnlineModeCheckbox);
		
		getSolo().clickOnView(connectivityState);
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		getSolo().sleep(2000);
		getSolo().clickOnView(connectivityState);
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_DISABLED);
		getSolo().sleep(2000);
		assertTrue(persistentStorage.isConnected());
	}

	public void testHTTPNotFoundStatusRightAfterAuthenticationWhenClickinOnShowRandomQuestion() {

		CheckBox connectivityState = (CheckBox) getSolo().getView(
				R.id.switchOnlineModeCheckbox);
		assertTrue("The checkbox should stay disabled", !connectivityState.isChecked());
		
		UserPreferences.getInstance().setSessionId("hahaFake");
		UserPreferences.getInstance().setConnectivityState(ConnectivityState.ONLINE);
		MockHttpClient mockClient = new MockHttpClient();
		mockClient.pushCannedResponse(".", HttpStatus.SC_NOT_FOUND, "", "");
		SwengHttpClientFactory.setInstance(mockClient);
		
		getSolo().clickOnButton("Show a random question.");
		
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		getSolo().getCurrentActivity().finish();
		getSolo().sleep(1000);
		
		assertTrue("The checkbox should stay disabled", !connectivityState.isChecked());
		getSolo().clickOnView(connectivityState);
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		assertTrue("The checkbox has not switched in offline mode", connectivityState.isChecked());
	}
	
//	public void testSendingOrderIsFIFO() {
//		int expectedSize = QuestionsProxy.getInstance().getOutboxSize() + 1;
//		CheckBox connectivityState = (CheckBox) getSolo().getView(
//				R.id.switchOnlineModeCheckbox);
//		
//		// manually switch to offline mode
//		getSolo().clickOnView(connectivityState);
//		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
//		getSolo().sleep(300);
//		
//		// preparing the responses scenario
//		AdvancedMockHttpClient client = new AdvancedMockHttpClient();
//		client.pushCannedResponse(
//				".", HttpStatus.SC_CREATED, "", "");
//		client.pushCannedResponse(
//				".", AdvancedMockHttpClient.IOEXCEPTION_ERROR_CODE,"", "", true);
//		client.pushCannedResponse(
//				".", HttpStatus.SC_CREATED, "", "", true);
//		SwengHttpClientFactory.setInstance(client);
//		
//		// filling the outbox
//		QuestionsProxy.getInstance().addOutAndInbox(createFakeQuestion(
//				"Statement 1"));
//		QuestionsProxy.getInstance().addOutAndInbox(createFakeQuestion(
//				"Statement 2"));
//		
//		// let the test begin...
//		getSolo().clickOnView(connectivityState);
//		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
//		getSolo().sleep(500);
//		
//		assertEquals(expectedSize, QuestionsProxy.getInstance().getOutboxSize());
//		assertEquals("Only submitted question should be 'Statement 2'",
//				"Statement 2", client.getLastSubmittedQuestionStatement());
//		
//		getSolo().clickOnView(connectivityState);
//		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_DISABLED);
//		getSolo().sleep(500);
//		
//		assertEquals(0, QuestionsProxy.getInstance().getOutboxSize());
//	}
	
//	public void testUncheckingBoxEmptiesOutbox() {
//		CheckBox connectivityState = (CheckBox) getSolo().getView(
//				R.id.switchOnlineModeCheckbox);
//		setSimpleMockClient(HttpStatus.SC_CREATED);
//		
//		// 1. manually switching to offline state
//		getSolo().clickOnView(connectivityState);
//		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
//		getSolo().sleep(500);
//		
//		// 2. adding stuff to the outbox
//		QuestionsProxy.getInstance().
//				addOutAndInbox(createFakeQuestion("Robotium?"));
//		
//		// 3. let's test 
//		getSolo().clickOnView(connectivityState);
//		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_DISABLED);
//		getSolo().sleep(500);
//		
//		assertEquals("Outbox should be empty after going from offline to online",
//				0, QuestionsProxy.getInstance().getOutboxSize());
//	}

//	private QuizQuestion createFakeQuestion(String questionStatement) {
//		List<String> answers = new ArrayList<String>();
//		answers.add("100% accurate");
//		answers.add("Fully voodoo and could generate non-pseudorandom numbers");
//		
//		Set<String> tags = new HashSet<String>();
//		tags.add("robotium");
//		tags.add("testing");
//		
//		QuizQuestion question = new QuizQuestion(
//				questionStatement, answers, 1, tags);
//		return question;
//	}

	/**
	 * Sets the instance of the {@link SwengHttpClientFactory} to a simple
	 * {@link MockHttpClient} answering all the requests with the specified
	 * status.
	 * 
	 * @param status The status answered with.
	 */

//	private void setSimpleMockClient(int status) {
//		MockHttpClient client = new MockHttpClient();
//		SwengHttpClientFactory.setInstance(client);
//		client.pushCannedResponse(".", status, "", "");
//	}
}
