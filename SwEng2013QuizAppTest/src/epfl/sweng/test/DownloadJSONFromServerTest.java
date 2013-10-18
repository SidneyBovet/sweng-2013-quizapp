package epfl.sweng.test;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;

import epfl.sweng.servercomm.JSONDownloader;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;

public class DownloadJSONFromServerTest extends TestCase{
	
	private MockHttpClient mockClient;
	
	@Override
	public void setUp() {
		mockClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(mockClient);
	}

	public void testGoodPathIsOkay() {
		JSONDownloader downloader = new JSONDownloader();
		mockClient.pushCannedResponse(
                "GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
                HttpStatus.SC_OK,
                "{\"question\": \"What is the answer to life, the universe, and everything?\","
                + " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
                + " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
                "application/json");
		downloader.execute("https://sweng-quiz.appspot.com/quizquestions/random");
		assert true;
	}
	
	public void testIOExceptionIsHandled() {
		mockClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				MockHttpClient.IOEXCEPTION_ERROR_CODE,
				"", "");
		new JSONDownloader().
			execute("https://sweng-quiz.appspot.com/quizquestions/random");
		assert true;
	}
	
	public void testProtocolExceptionIsHandled() {
		mockClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				MockHttpClient.CLIENTPROTOCOLEXCEPTION_ERROR_CODE,
				"", "");
		
		new JSONDownloader().
			execute("https://sweng-quiz.appspot.com/quizquestions/random");
		assert true;
	}
	
}
