
package epfl.sweng.test.servercomm;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.servercomm.AuthenticationProcess;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;

public class AuthenticationProcessTest extends
		ActivityInstrumentationTestCase2<AuthenticationActivity> {

	private Context contextOfActivity;
	private UserPreferences persistentStorage;
	private MockHttpClient mockClient;

	public AuthenticationProcessTest() {
		super(AuthenticationActivity.class);
	}

	@Override
	public void setUp() {
		mockClient = new MockHttpClient();
		contextOfActivity = getInstrumentation().getTargetContext();
		SwengHttpClientFactory.setInstance(mockClient);
		persistentStorage = UserPreferences
				.getInstance(contextOfActivity);
		persistentStorage.destroyAuthentication();
	}

	@Override
	public void tearDown() {

	}

	public void testDoInBackground() {
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

		//AuthenticationProcess authProcess = new AuthenticationProcess(
			//	contextOfActivity);
		//authProcess.execute("mockUsername", "mockPassword");

		String sessionID = AuthenticationProcess.
				authenticate("mockUsername", "mockPassword");
		assertEquals("SessssionID", sessionID);
		
		// TODO MUST ADD TIME IN ORDER TO LET THE PROCESS EXECUTE.
		// XXX le problème ici est que l'on doit passer un context a
		// AuthenticationProcess. Et je ne sais pas
		// pourquoi lors d'un test si on passe le context , cela donne une
		// erreur.

		//assertTrue("Must be logged in", persistentStorage.isAuthenticated());

		//
		// authProcess.execute("mockUsername", "mockPassword");
		//
		// try {
		// assertTrue("Should be ", authProcess.get() != null);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// } catch (ExecutionException e) {
		// e.printStackTrace();
		// }
	}
}

