package epfl.sweng.test;

import android.widget.Button;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class MainActivityNotAuthenticatedTest extends GUITest<MainActivity> {

	public MainActivityNotAuthenticatedTest() {
		super(MainActivity.class);
	}
	
	@Override
	protected void setUp() {
		super.setUp();
		// add stuff we need
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
		Button showButton = getSolo().getButton("Show a random question.");
		Button submitButton = getSolo().getButton("Submit a quiz question.");
		assertFalse(showButton.isEnabled());
		assertFalse(submitButton.isEnabled());
	}
}