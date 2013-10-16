package epfl.sweng.test;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;

import epfl.sweng.servercomm.DownloadJSONFromServer;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;

import junit.framework.TestCase;

public class DownloadJSONFromServerTest extends TestCase{
	
	private MockHttpClient mockClient;
	
	@Override
	public void setUp() {
		mockClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(mockClient);
	}

	public void testGoodPathIsOkay() {
		DownloadJSONFromServer downloader = new DownloadJSONFromServer();
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
		IOException exception = new IOException("Take this!");
		mockClient.setIOExceptionToThrow(exception);
		new DownloadJSONFromServer().
			execute("https://sweng-quiz.appspot.com/quizquestions/random");
		assert true;
	}
	
	public void testProtocolExceptionIsHandled() {
		ProtocolException exception = new ProtocolException("Take that!");
		mockClient.setProtExceptionToThrow(exception);
		new DownloadJSONFromServer().
			execute("https://sweng-quiz.appspot.com/quizquestions/random");
		assert true;
	}
	
}
