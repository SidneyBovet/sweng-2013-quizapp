package epfl.sweng.test.activities;

import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import epfl.sweng.R;
import epfl.sweng.comm.ConnectivityState;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class MainActivityNotAuthenticatedTest extends GUITest<MainActivity> {

	private UserPreferences persistentStorage;
	
	public MainActivityNotAuthenticatedTest() {
		super(MainActivity.class);
	}
	
	@Override
	protected void setUp() {
		super.setUp();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			fail("wtf");
		}
		persistentStorage = UserPreferences.getInstance();
		persistentStorage.destroyAuthentication();
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
	}

	@Override
	protected void tearDown() {
		try {
			super.tearDown();
			persistentStorage = UserPreferences.getInstance();
			UserPreferences.getInstance()
					.setConnectivityState(ConnectivityState.OFFLINE);
			persistentStorage.destroyAuthentication();
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Problem when hard using" +
					"the super to tear down the test", e);
			fail("Exception when tearing down the test");
		}

	}
	
	public void testShowAllButtons() {
		getSolo().sleep(500);
		assertTrue("Login Button is present",
				getSolo().searchButton("Log\\ in\\ using\\ Tequila"));
		assertTrue("Show Random Question Button is present",
				getSolo().searchButton("Show a random question."));
		assertTrue("Submit Quizz Question Button is present",
				getSolo().searchButton("Submit a quiz question."));
		assertTrue("Check Box should be present", 
				getSolo().searchText("Offline mode"));
		assertTrue("Search Button should be present", 
				getSolo().searchButton("Search"));
	}
	
	public void testQuestionButtonsAreDisabledAtBeggining() {
		Button logButton = getSolo().getButton("Log\\ in\\ using\\ Tequila");
		Button showButton = getSolo().getButton("Show\\ a\\ random\\ question.");
		Button submitButton = getSolo().getButton("Submit\\ a\\ quiz\\ question.");
		Button searchButton = getSolo().getButton("Search");
		assertTrue(logButton.isEnabled());
		assertFalse(showButton.isEnabled());
		assertFalse(submitButton.isEnabled());
		assertFalse(submitButton.isEnabled());
		assertFalse(searchButton.isEnabled());
	}
	
	public void testLogInButton() {
		assertFalse(persistentStorage.isAuthenticated());
		getSolo().clickOnButton("Log\\ in\\ using\\ Tequila");
		getSolo().sleep(2000);
		assertTrue("Password Asked", getSolo().searchText("GASPAR Password"));
		getSolo().goBack();
		getSolo().sleep(2000);
		assertTrue("Show Random Question Button is present",
				getSolo().searchButton("Show a random question."));
	}
	
	public void testCheckBoxInvisible() {
 		assertTrue("Check Box should be present", 
				getSolo().searchText("Offline mode"));
		CheckBox connexionState = (CheckBox) getSolo().getView(R.id.switch_offline_mode_checkbox);
		if (null == connexionState) {
			fail("R.id.switchOnlineModeCheckbox cannot be fetched by Robotium!");
		}
		assertFalse(connexionState.isShown());
	}
}
