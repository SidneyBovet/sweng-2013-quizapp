/*package epfl.sweng.test.activities;

import android.content.Context;
import android.widget.Button;
import android.widget.CheckBox;
import epfl.sweng.R;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.patterns.ConnectivityState;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class MainActivityNotAuthenticatedTest extends GUITest<MainActivity> {

	private Context contextOfMainActivity;
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
		contextOfMainActivity = getInstrumentation()
				.getTargetContext();
		persistentStorage = UserPreferences.
				getInstance(contextOfMainActivity);
		persistentStorage.destroyAuthentication();
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
	}

	@Override
	protected void tearDown() {
		
		try {
			super.tearDown();
			contextOfMainActivity = getInstrumentation()
					.getTargetContext();
			persistentStorage = UserPreferences.
					getInstance(contextOfMainActivity);
			UserPreferences.getInstance(getInstrumentation().getContext()).
				setConnectivityState(ConnectivityState.OFFLINE);
			persistentStorage.destroyAuthentication();
		} catch (Exception e1) {
			e1.printStackTrace();
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
		CheckBox connexionState = (CheckBox) getSolo().getView(R.id.switchOnlineModeCheckbox);
		if (null == connexionState) {
			fail("R.id.switchOnlineModeCheckbox cannot be fetched by Robotium!");
		}
		assertFalse(connexionState.isShown());
	}
}*/
