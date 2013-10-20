package epfl.sweng.test;

import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;

import epfl.sweng.servercomm.JSONDownloader;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;

public class JSONDownloaderTest extends TestCase {
	
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
		try {
			downloader.get();
		} catch (InterruptedException e) {
			fail("What a Terrible Failure (aka. WTF!?)");
		} catch (ExecutionException e) {
			fail("What a Terrible Failure (aka. WTF!?)");
		}
	}
	
	public void testIOExceptionIsHandled() {
		mockClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				MockHttpClient.IOEXCEPTION_ERROR_CODE,
				"", "");
		JSONDownloader downloader = new JSONDownloader();
		downloader.
			execute("https://sweng-quiz.appspot.com/quizquestions/random");
		try {
			downloader.get();
		} catch (InterruptedException e) {
			fail("What a Terrible Failure (aka. WTF!?)");
		} catch (ExecutionException e) {
			fail("What a Terrible Failure (aka. WTF!?)");
		}
	}
	
	public void testProtocolExceptionIsHandled() {
		mockClient.pushCannedResponse(
				"GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
				MockHttpClient.CLIENTPROTOCOLEXCEPTION_ERROR_CODE,
				"", "");
		JSONDownloader downloader = new JSONDownloader();
		downloader.
			execute("https://sweng-quiz.appspot.com/quizquestions/random");
		try {
			downloader.get();
		} catch (InterruptedException e) {
			fail("What a Terrible Failure (aka. WTF!?)");
		} catch (ExecutionException e) {
			fail("What a Terrible Failure (aka. WTF!?)");
		}
	}
	/* --- hard to compare two JSONObjects...
	public void testMultipleURLS() {
		String json = "{\"question\": \"What is the answer to life, the universe, and everything?\","
                + " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
                + " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }";
		mockClient.pushCannedResponse(
                "GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
                HttpStatus.SC_OK,
                json,
                "application/json");

		JSONDownloader downloader = new JSONDownloader();
		downloader.execute("https://sweng-quiz.appspot.com/quizquestions/random",
				"https://www.google.com", "https://www.facebook.com",
				"http://sweng.com");
		try {
			assertEquals(downloader.get(), json);
		} catch (InterruptedException e) {
			fail("An interruption has occurred during the execution.");
		} catch (ExecutionException e) {
			fail("An exception has occurred during the execution.");
		}
	}*/
}
