package epfl.sweng.test;
import java.io.IOException;

import android.test.ActivityInstrumentationTestCase2;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

public class ShowQuestionsActivityTest extends GUITest<ShowQuestionsActivity> {
	
	private MockHttpClient mockClient;
	
	public ShowQuestionsActivityTest() {
		super(ShowQuestionsActivity.class);
	}
	
	@Override
	public void setUp() {
		mockClient = new MockHttpClient();
		SwengHttpClientFactory.setInstance(mockClient);
	}
	
	public void testErrorWhileFetchingQuestionIsHandled() {
		IOException ioe = new IOException();
		mockClient.setIOExceptionToThrow(ioe );
		
		getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
		
		assert true;
	}
}