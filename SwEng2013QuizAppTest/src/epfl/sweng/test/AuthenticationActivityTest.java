package epfl.sweng.test;

import android.widget.Button;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

import epfl.sweng.entry.AuthenticationActivity;
import epfl.sweng.testing.TestCoordinator;
import epfl.sweng.testing.TestCoordinator.TTChecks;
import epfl.sweng.testing.TestingTransaction;

public class AuthenticationActivityTest extends GUITest<AuthenticationActivity> {

	public AuthenticationActivityTest() {
		super(AuthenticationActivity.class);
	}

	@Override
	protected void setUp() {
		super.setUp();
		// add stuff we need
	}

	public void testShowEditTextAndLoginButton() {
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		assertTrue("Username asked", getSolo().searchText("GASPAR Username"));
		assertTrue("Password Asked", getSolo().searchText("GASPAR Password"));
		assertTrue("Login Button is present",
				getSolo().searchButton("Log in using Tequila"));
	}

	public void testLoginButtonIsDisabledAtBeggining() {
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		Button loginButton = getSolo().getButton("Log in using Tequila");
		assertFalse(loginButton.isEnabled());
	}

	public void testLoginButtonIsEnableWhenLoginAndPasswordFieldsAreCorrectlyCompleted() {
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		EditText login = (EditText) getSolo().getEditText("GASPAR Username");
		EditText password = (EditText) getSolo().getEditText("GASPAR Password");
		Button loginButton = (Button) getSolo().getButton(
				"Log in using Tequila");
		getSolo().enterText(login, "     ");
		getSolo().enterText(password, "     ");

		assertFalse("login and password fileds must be correctly filled",
				loginButton.isEnabled());
	}

}
