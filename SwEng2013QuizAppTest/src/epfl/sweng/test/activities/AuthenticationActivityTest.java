package epfl.sweng.test.activities;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class AuthenticationActivityTest extends GUITest<AuthenticationActivity> {

	private Context contextOfAuthenticationActivity;
	private UserPreferences persistentStorage;
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
		persistentStorage = UserPreferences
				.getInstance(contextOfAuthenticationActivity);
		getActivityAndWaitFor(TTChecks.AUTHENTICATION_ACTIVITY_SHOWN);
	}

	public void testShowEditTextAndLoginButton() {
		assertTrue("Username asked", getSolo().searchText("GASPAR Username"));
		assertTrue("Password Asked", getSolo().searchText("GASPAR Password"));
		assertTrue("Login Button is present",
				getSolo().searchButton("Log in using Tequila"));
	}

	public void testLoginButtonIsDisabledAtBeggining() {
		Button loginButton = getSolo().getButton("Log in using Tequila");
		assertFalse(loginButton.isEnabled());
	}

	public void testLoginButtonIsEnableWhenLoginAndPasswordFieldsAreCorrectlyCompleted() {
		EditText login = (EditText) getSolo().getEditText("GASPAR Username");
		EditText password = (EditText) getSolo().getEditText("GASPAR Password");
		Button loginButton = (Button) getSolo().getButton(
				"Log in using Tequila");
		getSolo().enterText(login, "     ");
		getSolo().enterText(password, "     ");

		assertTrue("login and password fileds must be correctly filled",
				loginButton.isEnabled());
	}

	public void testAuthenticationActivityWorks() {
		persistentStorage.destroyAuthentication();
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
		getSolo().sleep(1000);

		assertTrue("Must be logged in", persistentStorage.isAuthenticated());
		getSolo().sleep(1000);
		getSolo().sleep(1);
		// getSolo().assertCurrentActivity("Must Be MainActivity",
		// MainActivity.class);

	}
	
	public void testGoBackToMainActivity() {
		//TODO same things as in the test testAuthenticationActivityWorks but
		//then test that you are in the MainActivity
	}

}
