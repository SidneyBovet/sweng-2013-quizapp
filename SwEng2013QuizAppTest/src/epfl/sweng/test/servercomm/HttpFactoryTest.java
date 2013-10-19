package epfl.sweng.test.servercomm;

import epfl.sweng.authentication.UserCredentialsStorage;
import epfl.sweng.servercomm.HttpFactory;
import junit.framework.TestCase;

public class HttpFactoryTest extends TestCase {
	
	// We use this to see if the addresses have been changed.
	private static final String SWENG_BASE_ADDRESS =
			"https://sweng-quiz.appspot.com";
	private static final String SWENG_FETCH_QUESTION =
			"https://sweng-quiz.appspot.com/quizquestions/random";
	private static final String SWENG_LOGIN =
			"https://sweng-quiz.appspot.com/login";
	private static final String TEQUILA_LOGIN =
			"https://tequila.epfl.ch/cgi-bin/tequila/login";
	
	@Override
	public void setUp() {
		
	}
	
	@Override
	public void tearDown() {
		
	}
	
	public void testGetSwengBaseAddress() {
		assertEquals(SWENG_BASE_ADDRESS, HttpFactory.getSwengBaseAddress());
	}
	
	public void testGetSwengFetchQuestion() {
		assertEquals(SWENG_FETCH_QUESTION, HttpFactory.getSwengFetchQuestion());
	}
	
	public void testGetSwengLogin() {
		assertEquals(SWENG_LOGIN, HttpFactory.getSwengLogin());
	}
	
	public void testGetTequilaLogin() {
		assertEquals(TEQUILA_LOGIN, HttpFactory.getTequilaLogin());
	}
	
	public void testgetGETRequest() {
//		String url = "google.com";
//		UserCredentialsStorage storageInstance = UserCredentialsStorage.getInstance();
//		assertTrue(HttpFactory.getGetRequest(url).containsHeader("Authorization"));
	}
}
