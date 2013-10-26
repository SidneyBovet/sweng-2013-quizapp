package epfl.sweng.test.activities;

import android.content.Context;
import android.widget.CheckBox;
import epfl.sweng.R;
import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.entry.MainActivity;
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
		persistentStorage.createEntry("SESSION_ID", "blabla");
		getActivityAndWaitFor(TTChecks.MAIN_ACTIVITY_SHOWN);
	}
	
	@Override
	protected void tearDown() throws Exception {
		persistentStorage.destroyAuthentication();
		super.tearDown();
	};
	
	public void testBeginConnected() {
		assertTrue(persistentStorage.isConnected());
	}
	
	public void testCheckBoxCheckDisconnected() {
		CheckBox connexionState = (CheckBox) getSolo().getView(R.id.switchOnlineModeCheckbox);
		getSolo().clickOnView(connexionState);
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		assertFalse(persistentStorage.isConnected());
	}
	
	public void testCheckBoxCheckConnected() {
		CheckBox connexionState = (CheckBox) getSolo().getView(R.id.switchOnlineModeCheckbox);
		getSolo().clickOnView(connexionState);
		getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_ENABLED);
		getSolo().clickOnView(connexionState);
		//XXX why doesn't work with TTCHECKS but with sleep
		//getActivityAndWaitFor(TTChecks.OFFLINE_CHECKBOX_DISABLED);
		getSolo().sleep(2000);
		assertTrue(persistentStorage.isConnected());
	}
}
