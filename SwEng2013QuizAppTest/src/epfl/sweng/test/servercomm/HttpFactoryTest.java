package epfl.sweng.test.servercomm;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import junit.framework.TestCase;
import epfl.sweng.servercomm.HttpFactory;

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

	public void testGetGetRequestWithoutContext() {
		HttpGet get = new HttpGet("lol");
		assertEquals(get.getRequestLine().toString(),
				HttpFactory.getGetRequest("lol").getRequestLine().toString());
	}

	public void testGetPostRequestWithoutContext() {
		HttpPost post = new HttpPost("lol");
		assertEquals(post.getRequestLine().toString(),
				HttpFactory.getPostRequest("lol").getRequestLine().toString());
	}
}
