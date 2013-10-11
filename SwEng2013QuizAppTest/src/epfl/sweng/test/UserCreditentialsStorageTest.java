package epfl.sweng.test;
import android.test.ActivityInstrumentationTestCase2;
import epfl.sweng.backend.UserCreditentialsStorage;
import epfl.sweng.entry.MainActivity;

public class UserCreditentialsStorageTest extends ActivityInstrumentationTestCase2<MainActivity> {

	public UserCreditentialsStorageTest(
			Class<MainActivity> activityClass) {
		super(activityClass);
		/*TODO  get context of mainactivity
		 * UserCreditentialsStorage persistentStorage =
				UserCreditentialsStorage.getSingletonInstanceOfStorage(
				MainActivity.);*/
	}
	
	public void testHello() {
		fail("Not yet implemented");
	}	

}
