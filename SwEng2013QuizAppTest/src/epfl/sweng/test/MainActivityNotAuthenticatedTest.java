package epfl.sweng.test;

import android.content.Context;
import android.widget.Button;
import epfl.sweng.backend.UserCredentialsStorage;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class MainActivityNotAuthenticatedTest extends GUITest<MainActivity> {

	private Context contextOfMainActivity;
	private UserCredentialsStorage persistentStorage;
	
	public MainActivityNotAuthenticatedTest() {
		super(MainActivity.class);
	}
	
	@Override
	protected void setUp() {
		super.setUp();
		contextOfMainActivity = getInstrumentation()
				.getTargetContext();
		persistentStorage = UserCredentialsStorage.
				getInstance(contextOfMainActivity);
		persistentStorage.releaseAuthentication();
	}
	
	public void testShowAllButtons() {
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		assertTrue("Login Button is present",
				getSolo().searchButton("Log in using Tequila"));
		assertTrue("Show Random Question Button is present",
				getSolo().searchButton("Show a random question."));
		assertTrue("Submit Quizz Question Button is present",
				getSolo().searchButton("Submit a quiz question."));
	}
	
	public void testQuestionButtonsAreDisabledAtBeggining() {
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		Button logButton = getSolo().getButton("Log in using Tequila");
		Button showButton = getSolo().getButton("Show a random question.");
		Button submitButton = getSolo().getButton("Submit a quiz question.");
		assertTrue(logButton.isEnabled());
		assertFalse(showButton.isEnabled());
		assertFalse(submitButton.isEnabled());
	}
	
	public void testLogInButton() {
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
		assertFalse(persistentStorage.isAuthenticated());
		getSolo().sleep(2000);
		getSolo().clickOnButton("Log in using Tequila");
		getSolo().sleep(2000);
		assertTrue("Password Asked", getSolo().searchText("GASPAR Password"));
		getSolo().goBack();
		getSolo().sleep(2000);
		assertTrue("Show Random Question Button is present",
				getSolo().searchButton("Show a random question."));
	}
	
}
