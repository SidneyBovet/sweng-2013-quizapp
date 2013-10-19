package epfl.sweng.test;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;
import junit.framework.TestCase;

public class MockHttpClientTest extends TestCase{
	private MockHttpClient mockClient;
	@Override
	public void setUp() {
		mockClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(mockClient);
	}
	
	public void testThrowsAnException() {
		mockClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				MockHttpClient.CLIENTPROTOCOLEXCEPTION_ERROR_CODE,
				"", "");
		HttpGet request = new HttpGet("https://sweng-quiz.appspot.com/quizquestions/random");
		try {
			SwengHttpClientFactory.getInstance().
					execute(request);
		} catch (ClientProtocolException e) {
			return;
		} catch (IOException e) {
			fail("Not a CPE...");
		}
		fail("no exception thrown");
	}
	
	@Override
	public void tearDown() throws Exception {
		while (!mockClient.responsesIsEmpty()) {
			mockClient.popCannedResponse();
		}
		
		super.tearDown();
	}
}
