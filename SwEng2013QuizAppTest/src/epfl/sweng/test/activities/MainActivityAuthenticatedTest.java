package epfl.sweng.test.activities;

import android.widget.Button;
import android.widget.CheckBox;
import epfl.sweng.R;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class MainActivityAuthenticatedTest extends GUITest<MainActivity> {

	private UserPreferences persistentStorage;
	
	public MainActivityAuthenticatedTest() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() {
		super.setUp();
		persistentStorage = UserPreferences.getInstance();
		persistentStorage.setSessionId("blabla");
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		persistentStorage = UserPreferences.getInstance();
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
	
	public void testSearch() {
		getSolo().clickOnButton("Search");
		getActivityAndWaitFor(TTChecks.SEARCH_ACTIVITY_SHOWN);
		assertTrue("Search Button should be present",
				getSolo().searchButton("Search"));
		Button searchButton = getSolo().getButton("Search");
		assertFalse(searchButton.isEnabled());
		getSolo().goBack();
	}
	
	public void testShowAllButtons() {
		getSolo().sleep(500);
		assertTrue("Logout Button should be present",
				getSolo().searchButton("Log\\ out"));
		assertTrue("Show Random Question Button should be present",
				getSolo().searchButton("Show\\ a\\ random\\ question."));
		assertTrue("Submit Quizz Question Button should be present",
				getSolo().searchButton("Submit\\ a\\ quiz\\ question."));
		assertTrue("Check Box should be present", 
				getSolo().searchText("Offline\\ mode"));
		assertTrue("Search Button should be present", 
				getSolo().searchButton("Search"));
	}
	
	public void testQuestionButtonsAreEnabledAtBeggining() {
		Button logButton = getSolo().getButton("Log\\ out");
		Button showButton = getSolo().getButton("Show\\ a\\ random\\ question.");
		Button submitButton = getSolo().getButton("Submit\\ a\\ quiz\\ question.");
		Button searchButton = getSolo().getButton("Search");
		assertTrue(logButton.isEnabled());
		assertTrue(showButton.isEnabled());
		assertTrue(submitButton.isEnabled());
		assertTrue(submitButton.isEnabled());
		assertTrue(searchButton.isEnabled());
	}
	
	public void testCheckBoxVisible() {
		CheckBox connexionState = (CheckBox) getSolo().getView(R.id.switchOnlineModeCheckbox);
		if (null == connexionState) {
			fail("R.id.switchOnlineModeCheckbox not found by Robotium!");
		}
		assertTrue(connexionState.isShown());
	}

}
