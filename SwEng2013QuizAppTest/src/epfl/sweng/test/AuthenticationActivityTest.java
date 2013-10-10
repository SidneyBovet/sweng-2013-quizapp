package epfl.sweng.test;

import android.test.ActivityInstrumentationTestCase2;
import epfl.sweng.entry.AuthenticationActivity;

public class AuthenticationActivityTest extends
		ActivityInstrumentationTestCase2<AuthenticationActivity> {

	public AuthenticationActivityTest() {
		super(AuthenticationActivity.class);

	}

	public void testhello() {
		assertEquals("a", "a");
	}

}
