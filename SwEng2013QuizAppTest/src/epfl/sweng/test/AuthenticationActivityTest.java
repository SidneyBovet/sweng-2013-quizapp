package epfl.sweng.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;
import epfl.sweng.entry.AuthenticationActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import epfl.sweng.testing.TestingTransaction;

public class AuthenticationActivityTest extends
		ActivityInstrumentationTestCase2<AuthenticationActivity> {

	private Solo solo;

	public AuthenticationActivityTest() {
		super(AuthenticationActivity.class);
	}

	@Override
	protected void setUp() {
		solo = new Solo(getInstrumentation());
	}

	public void testShowEditTextAndLoginButton() {
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		assertTrue("Username asked", solo.searchText("GASPAR Username"));
		assertTrue("Password Asked", solo.searchText("GASPAR Password"));
		assertTrue("Login Button is present",
				solo.searchButton("Log in using Tequila"));
	}

	public void testLoginButtonIsDisabledAtBeggining() {
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		Button loginButton = solo.getButton("Log in using Tequila");
		assertFalse(loginButton.isEnabled());
	}


	public void testLoginButtonIsEnableWhenLoginAndPasswordFieldsAreCorrectlyCompleted() {
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		EditText login = (EditText) solo.getEditText("GASPAR Username");
		EditText password = (EditText) solo.getEditText("GASPAR Password");
		Button loginButton = (Button) solo.getButton("Log in using Tequila");
		solo.enterText(login, "     ");
		solo.enterText(password, "     ");
		
		assertFalse("login and password fileds must be correctly filled",
				loginButton.isEnabled());
	}

	private void getActivityAndWaitFor(
			final TestCoordinator.TTChecks expected) {
		TestCoordinator.run(getInstrumentation(), new TestingTransaction() {
			@Override
			public void initiate() {
				getActivity();
			}

			@Override
			public void verify(TestCoordinator.TTChecks notification) {
				assertEquals(String.format(
						"Expected notification %s, but received %s", expected,
						notification), expected, notification);
			}

			@Override
			public String toString() {
				return String.format("getActivityAndWaitFor(%s)", expected);
			}
		});
	}
}
