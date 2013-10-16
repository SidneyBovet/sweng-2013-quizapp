package epfl.sweng.test;

import android.content.Context;
import android.widget.Button;
import epfl.sweng.backend.UserCredentialsStorage;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class MainActivityAuthenticatedTest extends GUITest<MainActivity> {

	private Context contextOfMainActivity;
	private UserCredentialsStorage persistentStorage;
	
	public MainActivityAuthenticatedTest() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() {
		super.setUp();
		contextOfMainActivity = getInstrumentation()
				.getTargetContext();
		persistentStorage = UserCredentialsStorage.
				getInstance(contextOfMainActivity);
		persistentStorage.takeAuthentication("blabla");
	}
	
	@Override
	protected void tearDown() throws Exception {
		persistentStorage.releaseAuthentication();
		super.tearDown();
	};
	
	public void testShowAllButtons() {
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		assertTrue("Login Button is present",
				getSolo().searchButton("Log out"));
		assertTrue("Show Random Question Button is present",
				getSolo().searchButton("Show a random question."));
		assertTrue("Submit Quizz Question Button is present",
				getSolo().searchButton("Submit a quiz question."));
	}
	
	public void testQuestionButtonsAreEnabledAtBeggining() {
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		Button showButton = getSolo().getButton("Show a random question.");
		Button submitButton = getSolo().getButton("Submit a quiz question.");
		assertTrue(showButton.isEnabled());
		assertTrue(submitButton.isEnabled());
	}

}
