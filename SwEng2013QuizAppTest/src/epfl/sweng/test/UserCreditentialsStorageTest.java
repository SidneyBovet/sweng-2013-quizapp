package epfl.sweng.test;
import android.test.ActivityInstrumentationTestCase2;
import epfl.sweng.entry.MainActivity;

public class UserCreditentialsStorageTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public UserCreditentialsStorageTest(
			Class<MainActivity> activityClass) {
		super(activityClass);
		/*UserCreditentialsStorage persistentStorage =
				UserCreditentialsStorage.getSingletonInstanceOfStorage(
				MainActivity.getApplicationContext());*/
	}
	
	public void testHello() {
		fail("Not yet implemented");
	}	

}
