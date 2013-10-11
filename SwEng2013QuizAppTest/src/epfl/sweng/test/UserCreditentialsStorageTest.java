package epfl.sweng.test;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import epfl.sweng.backend.UserCredentialsStorage;
import epfl.sweng.entry.MainActivity;

public class UserCreditentialsStorageTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private Context contextOfMainActivity;
	private UserCredentialsStorage persistentStorage;
	
	public UserCreditentialsStorageTest() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() {
		getActivity();
		contextOfMainActivity = getInstrumentation()
				.getTargetContext();
		persistentStorage = UserCredentialsStorage.
				getSingletonInstanceOfStorage(contextOfMainActivity);
	}
	
	public void testAuthentication() {
		String dummySessionID = "blabla";
		assertFalse(persistentStorage.isAuthenticated());
		persistentStorage.takeAuthentication(dummySessionID);
		assertTrue(persistentStorage.isAuthenticated());
		persistentStorage.releaseAuthentication();
	}

	public void testReleaseAuthentication() {
		String dummySessionID = "blabla2";
		assertFalse(persistentStorage.isAuthenticated());
		persistentStorage.takeAuthentication(dummySessionID);
		assertTrue(persistentStorage.isAuthenticated());
		persistentStorage.releaseAuthentication();
		assertFalse(persistentStorage.isAuthenticated());
	}
}
