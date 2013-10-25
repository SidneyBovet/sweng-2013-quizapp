package epfl.sweng.test.activities;

import android.content.Context;
import android.widget.Button;
import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class MainActivityAuthenticatedTest extends GUITest<MainActivity> {

	private Context contextOfMainActivity;
	private UserPreferences persistentStorage;
	
	public MainActivityAuthenticatedTest() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() {
		super.setUp();
		contextOfMainActivity = getInstrumentation()
				.getTargetContext();
		persistentStorage = UserPreferences.
				getInstance(contextOfMainActivity);
		persistentStorage.createEntry("blabla");
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
	}
	
	@Override
	protected void tearDown() throws Exception {
		persistentStorage.destroyAuthentication();
		super.tearDown();
	};
	
	public void testLogOut() {
		getSolo().clickOnButton("Log out");
		getActivityAndWaitFor(TTChecks.LOGGED_OUT);
		assertTrue("Submit Quizz Question Button should be present",
				getSolo().searchButton("Submit a quiz question."));
		assertTrue("Show Random Question Button should be present",
				getSolo().searchButton("Show a random question."));
		
		Button showButton = getSolo().getButton("Show a random question.");
		Button submitButton = getSolo().getButton("Submit a quiz question.");
		assertTrue(!showButton.isEnabled());
		assertTrue(!submitButton.isEnabled());
	}
	
	public void testShowAllButtons() {
		assertTrue("Login Button is present",
				getSolo().searchButton("Log out"));
		assertTrue("Show Random Question Button should be present",
				getSolo().searchButton("Show a random question."));
		assertTrue("Submit Quizz Question Button should be present",
				getSolo().searchButton("Submit a quiz question."));
	}
	
	public void testQuestionButtonsAreEnabledAtBeggining() {
		Button showButton = getSolo().getButton("Show a random question.");
		Button submitButton = getSolo().getButton("Submit a quiz question.");
		assertTrue(showButton.isEnabled());
		assertTrue(submitButton.isEnabled());
	}

}
