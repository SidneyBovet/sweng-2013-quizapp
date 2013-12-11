package epfl.sweng.test.minimalmock;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.util.Log;
import android.widget.EditText;
import epfl.sweng.editquestions.EditQuestionActivity;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.activities.GUITest;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class AdvancedMockHttpClientTest extends GUITest<EditQuestionActivity> {

	protected static final String RANDOM_QUESTION_BUTTON_LABEL = "Show a random question";

	private AdvancedMockHttpClient httpClient;

	public AdvancedMockHttpClientTest() {
		super(EditQuestionActivity.class);
	}

	@Override
	public void setUp() {
		super.setUp();
		httpClient = new AdvancedMockHttpClient();
		SwengHttpClientFactory.setInstance(httpClient);
		getActivityAndWaitFor(TTChecks.EDIT_QUESTIONS_SHOWN);
	}

	public void testResponseIsUsedOnlyOnce() {
		httpClient.pushCannedResponse(".", HttpStatus.SC_CREATED, "", "", true);

		fillFormWithCorrectQuestion("statement");
		getSolo().clickOnButton("Submit");

		getActivityAndWaitFor(TTChecks.NEW_QUESTION_SUBMITTED);

		assertEquals("Client's responses should be empty", 0,
				httpClient.getResponsesListSize());
	}

	public void testLastPostedQuestionIsStoredByClient() {
		getSolo().sleep(1000);
		final String statement = "Statement lolilol";
		httpClient.pushCannedResponse(".", HttpStatus.SC_CREATED, "", "");

		fillFormWithCorrectQuestion("not the good statement");
		getSolo().clickOnButton("Submit");
		getActivityAndWaitFor(TTChecks.NEW_QUESTION_SUBMITTED);
		getSolo().sleep(300);
		fillFormWithCorrectQuestion(statement);
		getSolo().clickOnButton("Submit");
		getActivityAndWaitFor(TTChecks.NEW_QUESTION_SUBMITTED);
		getSolo().sleep(300);

		assertEquals("Client should've stored the last question", statement,
				httpClient.getLastSubmittedQuestionStatement());
	}

	public void testOrderOfMatchingResponses() {
		httpClient.pushCannedResponse(".", HttpStatus.SC_CREATED, "", "", true);
		httpClient.pushCannedResponse(".",
				AdvancedMockHttpClient.IOEXCEPTION_ERROR_CODE, "", "", true);
		httpClient.pushCannedResponse(".",
				AdvancedMockHttpClient.CLIENTPROTOCOLEXCEPTION_ERROR_CODE, "",
				"", true);

		HttpUriRequest request = new HttpGet("http://fake.uri.lol");

		try {
			httpClient.execute(request);
		} catch (ClientProtocolException e) {
			Log.e(httpClient.getClass().getName(), "execute(): "
					+ "there was a ClientProtocolException ", e);
			// okay
		} catch (IOException e) {
			Log.e(httpClient.getClass().getName(), "execute(): "
					+ "there was a IOException ", e);
			fail("should be a CPE");
		}

		try {
			httpClient.execute(request);
		} catch (ClientProtocolException e) {
			Log.e(httpClient.getClass().getName(), "execute(): "
					+ "there was a ClientProtocolException ", e);
			fail("should be an IOE");
		} catch (IOException e) {
			Log.e(httpClient.getClass().getName(), "execute(): "
					+ "there was a IOException ", e);
			// okay
		}

		try {
			httpClient.execute(request);
		} catch (Exception e) {
			Log.e(httpClient.getClass().getName(), "execute(): "
					+ "there was a Exception", e);
			fail("should have returned SC_CREATED");
		}
	}

	private void fillFormWithCorrectQuestion(String statement) {
		getSolo().clickOnButton("\\+");
		waitFor(TTChecks.QUESTION_EDITED);
		getSolo().enterText(
				(EditText) getSolo().getText(
						"Type in the question\'s text body"), statement);

		getSolo().enterText(
				(EditText) getSolo().getText("Type in the question\'s tags"),
				"tag");

		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer D");
		getSolo().enterText((EditText) getSolo().getText("Type in the answer"),
				"answer BBBBBB");
		getSolo().clickOnButton("" + (char) 10008);
		getActivityAndWaitFor(TTChecks.QUESTION_EDITED);
		getSolo().sleep(100);
	}
}
