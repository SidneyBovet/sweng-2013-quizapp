package epfl.sweng.test;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.entry.MainActivity;

public class UserCreditentialsStorageTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private Context contextOfMainActivity;
	private UserPreferences persistentStorage;
	
	public UserCreditentialsStorageTest() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() {
		//getActivity();
		contextOfMainActivity = getInstrumentation()
				.getTargetContext();
		persistentStorage = UserPreferences.
				getInstance(contextOfMainActivity);
		persistentStorage.destroyAuthentication();
	}
	
	public void testAuthentication() {
		String dummySessionID = "blabla";
		assertFalse(persistentStorage.isAuthenticated());
		persistentStorage.createEntry("SESSION_ID", dummySessionID);
		assertTrue(persistentStorage.isAuthenticated());
		persistentStorage.destroyAuthentication();
	} 
	
	public void testReleaseAuthentication() {
		String dummySessionID = "blabla2";
		assertFalse(persistentStorage.isAuthenticated());
		persistentStorage.createEntry("SESSION_ID", dummySessionID);
		assertTrue(persistentStorage.isAuthenticated());
		persistentStorage.destroyAuthentication();
		assertFalse(persistentStorage.isAuthenticated());
	}
	
	public void testSingleton() {
		UserPreferences persistentStorage2 = UserPreferences.
				getInstance(contextOfMainActivity);
		assertTrue(persistentStorage.equals(persistentStorage2));
	} 
}
