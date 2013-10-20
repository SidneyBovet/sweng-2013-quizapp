package epfl.sweng.test.servercomm;

import java.util.concurrent.ExecutionException;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.authentication.UserCredentialsStorage;
import epfl.sweng.servercomm.AuthenticationProcess;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;

public class AuthenticationProcessTest extends
	ActivityInstrumentationTestCase2<AuthenticationActivity> {
	
	private Context contextOfActivity;
	private UserCredentialsStorage persistentStorage;
	private MockHttpClient mockClient;
	
	public AuthenticationProcessTest() {
		super(AuthenticationActivity.class);
		mockClient = new MockHttpClient();
		contextOfActivity = getInstrumentation().getTargetContext();
		SwengHttpClientFactory.setInstance(mockClient);
		persistentStorage = UserCredentialsStorage.getInstance(contextOfActivity);
	}
	
	@Override
	public void setUp() {
		persistentStorage.releaseAuthentication();
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

		assertTrue("Must be logged in", persistentStorage.isAuthenticated());
		
		AuthenticationProcess authProcess = 
				new AuthenticationProcess(contextOfActivity);
		
		authProcess.execute("mockUsername", "mockPassword");
		
		try {
			assertTrue("Should be ", authProcess.get() != null);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
