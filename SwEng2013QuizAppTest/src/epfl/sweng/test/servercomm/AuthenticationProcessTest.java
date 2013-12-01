
package epfl.sweng.test.servercomm;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import epfl.sweng.authentication.AuthenticationActivity;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.servercomm.AuthenticationProcess;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.AdvancedMockHttpClient;

public class AuthenticationProcessTest extends
		ActivityInstrumentationTestCase2<AuthenticationActivity> {

	private Context contextOfActivity;
	private UserPreferences persistentStorage;
	private AdvancedMockHttpClient mockClient;

	public AuthenticationProcessTest() {
		super(AuthenticationActivity.class);
	}

	@Override
	public void setUp() {
		mockClient = new AdvancedMockHttpClient();
		contextOfActivity = getInstrumentation().getTargetContext();
		SwengHttpClientFactory.setInstance(mockClient);
		persistentStorage = UserPreferences
				.getInstance(contextOfActivity);
		persistentStorage.destroyAuthentication();
	}

	@Override
	public void tearDown() {

	}
	
	public void testAuthenticationSuccessful() {
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
		
		String sessionID = AuthenticationProcess.
				authenticate("mockUsername", "mockPassword");
		assertEquals("SessssionID", sessionID);
	}

	public void testGetTokenWrongJSON() {
		mockClient.pushCannedResponse(
				"GET https://sweng-quiz.appspot.com/login ", HttpStatus.SC_OK,
				"not a single structure of JSON token was here", "application/json");
		
		String sessionID = AuthenticationProcess.
				authenticate("mockUsername", "mockPassword");
		
		assertEquals("SessionID should be null.", sessionID, null);
	}
	
	public void testGetTokenIOException() {
		mockClient.pushCannedResponse(
				".", AdvancedMockHttpClient.IOEXCEPTION_ERROR_CODE,
				"", "", true);
		
		String sessionID = AuthenticationProcess.
				authenticate("mockUsername", "mockPassword");
		
		assertEquals("SessionID should be null.", sessionID, null);
	}
	
	public void testGetTokenClientProtocolException() {
		mockClient.pushCannedResponse(
				".", AdvancedMockHttpClient.CLIENTPROTOCOLEXCEPTION_ERROR_CODE,
				"", "", true);
		
		String sessionID = AuthenticationProcess.
				authenticate("mockUsername", "mockPassword");
		
		assertEquals("SessionID should be null.", sessionID, null);
	}
	
	public void testValidateTokenClientProtocolException() {
		mockClient.pushCannedResponse(
				"GET https://sweng-quiz.appspot.com/login ", HttpStatus.SC_OK,
				"{\"token\": \"tooookkkeeeenn\"}", "application/json");
		mockClient.pushCannedResponse(
				"POST https://tequila.epfl.ch/cgi-bin/tequila/login",
				AdvancedMockHttpClient.CLIENTPROTOCOLEXCEPTION_ERROR_CODE,
				"", "", true);
		
		String sessionID = AuthenticationProcess.
				authenticate("mockUsername", "mockPassword");
		
		assertEquals("SessionID should be null.", sessionID, null);
	}
	
	public void testValidateTokenIOException() {
		mockClient.pushCannedResponse(
				"GET https://sweng-quiz.appspot.com/login ", HttpStatus.SC_OK,
				"{\"token\": \"tooookkkeeeenn\"}", "application/json");
		mockClient.pushCannedResponse(
				"POST https://tequila.epfl.ch/cgi-bin/tequila/login",
				AdvancedMockHttpClient.IOEXCEPTION_ERROR_CODE,
				"", "", true);
		
		String sessionID = AuthenticationProcess.
				authenticate("mockUsername", "mockPassword");
		
		assertEquals("SessionID should be null.", sessionID, null);
	}
	
	public void testSessionIDIOException() {
		mockClient.pushCannedResponse(
				"GET https://sweng-quiz.appspot.com/login ", HttpStatus.SC_OK,
				"{\"token\": \"tooookkkeeeenn\"}", "application/json");
		final int found = 302;
		mockClient.pushCannedResponse(
				"POST https://tequila.epfl.ch/cgi-bin/tequila/login", found,
				"", "HttpResponse");
		mockClient.pushCannedResponse(
				"POST https://sweng-quiz.appspot.com/login",
				AdvancedMockHttpClient.IOEXCEPTION_ERROR_CODE,
				"", "", true);
		
		String sessionID = AuthenticationProcess.
				authenticate("mockUsername", "mockPassword");
		
		assertEquals("SessionID should be null.", sessionID, null);
	}
	
	public void testSessionIDClientProtocolException() {
		mockClient.pushCannedResponse(
				"GET https://sweng-quiz.appspot.com/login ", HttpStatus.SC_OK,
				"{\"token\": \"tooookkkeeeenn\"}", "application/json");
		final int found = 302;
		mockClient.pushCannedResponse(
				"POST https://tequila.epfl.ch/cgi-bin/tequila/login", found,
				"", "HttpResponse");
		mockClient.pushCannedResponse(
				"POST https://sweng-quiz.appspot.com/login",
				AdvancedMockHttpClient.CLIENTPROTOCOLEXCEPTION_ERROR_CODE,
				"", "", true);
		
		String sessionID = AuthenticationProcess.
				authenticate("mockUsername", "mockPassword");
		
		assertEquals("SessionID should be null.", sessionID, null);
	}
	
	public void testSessionIDWrongJSON() {
		mockClient.pushCannedResponse(
				"GET https://sweng-quiz.appspot.com/login ", HttpStatus.SC_OK,
				"{\"token\": \"tooookkkeeeenn\"}", "application/json");
		final int found = 302;
		mockClient.pushCannedResponse(
				"POST https://tequila.epfl.ch/cgi-bin/tequila/login", found,
				"", "HttpResponse");
		mockClient.pushCannedResponse(
				"POST https://sweng-quiz.appspot.com/login", HttpStatus.SC_OK,
				"not a single structure of JSON sessionID was here", "application/json");
		
		String sessionID = AuthenticationProcess.
				authenticate("mockUsername", "mockPassword");
		
		assertEquals("SessionID should be null.", sessionID, null);
	}
}

