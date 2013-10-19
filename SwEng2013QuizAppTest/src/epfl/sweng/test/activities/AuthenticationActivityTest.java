package epfl.sweng.test.activities;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.authentication.UserCredentialsStorage;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class AuthenticationActivityTest extends GUITest<AuthenticationActivity> {

	private Context contextOfAuthenticationActivity;
	private UserCredentialsStorage persistentStorage;
	private MockHttpClient mockClient;

	public AuthenticationActivityTest() {
		super(AuthenticationActivity.class);
	}

	@Override
	protected void setUp() {
		super.setUp();
		mockClient = new MockHttpClient();
		contextOfAuthenticationActivity = getInstrumentation()
				.getTargetContext();
		SwengHttpClientFactory.setInstance(mockClient);
		persistentStorage = UserCredentialsStorage
				.getInstance(contextOfAuthenticationActivity);
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

	public void testAuthenticationActivityWorks() {
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
		persistentStorage.releaseAuthentication();
		getSolo().sleep(1);

		mockClient.pushCannedResponse(
				"GET https://sweng-quiz.appspot.com/login ", HttpStatus.SC_OK,
				"{\"token\": \"tooookkkeeeenn\"}", "application/json");
		final int found = 302;
		mockClient.pushCannedResponse(
				"POST https://tequila.epfl.ch/cgi-bin/tequila/login", found,
				"", "HttpResponse");
		mockClient.pushCannedResponse(
				"POST https://sweng-quiz.appspot.com/login", HttpStatus.SC_OK,
				"{\"session\": \"SessssionID\"}", "application/json");
		EditText login = (EditText) getSolo().getEditText("GASPAR Username");
		EditText password = (EditText) getSolo().getEditText("GASPAR Password");

		getSolo().enterText(login, "Bob");
		getSolo().enterText(password, "Alligator21");

		getSolo().clickOnButton("Log in using Tequila");
		final int slp = 1000;
		getSolo().sleep(slp);

		assertTrue("Must be logged in", persistentStorage.isAuthenticated());
		getSolo().sleep(slp);
		getSolo().sleep(1);
		// getSolo().assertCurrentActivity("Must Be MainActivity",
		// MainActivity.class);

	}
	
	public void testGoBackToMainActivity() {
		//TODO same things as in the test testAuthenticationActivityWorks but
		//then test that you are in the MainActivity
	}

}
