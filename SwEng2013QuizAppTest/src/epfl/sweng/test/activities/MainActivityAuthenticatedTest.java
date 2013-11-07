package epfl.sweng.test.activities;

import android.content.Context;
import android.widget.Button;
import android.widget.CheckBox;
import epfl.sweng.R;
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
		persistentStorage.setSessionId("blabla");
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		contextOfMainActivity = getInstrumentation()
				.getTargetContext();
		persistentStorage = UserPreferences.
				getInstance(contextOfMainActivity);
		persistentStorage.destroyAuthentication();
	};
	
	public void testLogOut() {
		getSolo().clickOnButton("Log\\ out");
		getActivityAndWaitFor(TTChecks.LOGGED_OUT);
		
		Button showButton = getSolo().getButton("Show\\ a\\ random\\ question.");
		Button submitButton = getSolo().getButton("Submit\\ a\\ quiz\\ question.");
		assertFalse(showButton.isEnabled());
		assertFalse(submitButton.isEnabled());
	}
	
	public void testShowAllButtons() {
		assertTrue("Logout Button should be present",
				getSolo().searchButton("Log\\ out"));
		assertTrue("Show Random Question Button should be present",
				getSolo().searchButton("Show\\ a\\ random\\ question."));
		assertTrue("Submit Quizz Question Button should be present",
				getSolo().searchButton("Submit\\ a\\ quiz\\ question."));
		assertTrue("Check Box should be present", 
				getSolo().searchText("Offline\\ mode"));
	}
	
	public void testQuestionButtonsAreEnabledAtBeggining() {
		Button showButton = getSolo().getButton("Show\\ a\\ random\\ question.");
		Button submitButton = getSolo().getButton("Submit\\ a\\ quiz\\ question.");
		assertTrue(showButton.isEnabled());
		assertTrue(submitButton.isEnabled());
	}
	
	public void testCheckBoxVisible() {
		CheckBox connexionState = (CheckBox) getSolo().getView(R.id.switchOnlineModeCheckbox);
		if (null == connexionState) {
			fail("R.id.switchOnlineModeCheckbox not found by Robotium!");
		}
		assertTrue(connexionState.isShown());
	}

}
