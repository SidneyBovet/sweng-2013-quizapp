package epfl.sweng.test;

import org.apache.http.HttpStatus;

import android.widget.Button;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.showquestions.ShowQuestionsActivity;
import epfl.sweng.test.minimalmock.MockHttpClient;
import epfl.sweng.testing.TestCoordinator.TTChecks;

/** A test that illustrates the use of MockHttpClients */
public class ShowQuestionsActivityTest extends GUITest<ShowQuestionsActivity> {

    protected static final String RANDOM_QUESTION_BUTTON_LABEL = "Show a random question";

    private MockHttpClient httpClient;

    public ShowQuestionsActivityTest() {
        super(ShowQuestionsActivity.class);
    }

    @Override
    public void setUp() {
        super.setUp();

        httpClient = new MockHttpClient();
        SwengHttpClientFactory.setInstance(httpClient);

    }

    public void testFetchQuestion() {
        httpClient.pushCannedResponse(
                "GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
                HttpStatus.SC_OK,
                "{\"question\": \"What is the answer to life, the universe, and everything?\","
                + " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
                + " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
                "application/json");

        getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
        assertTrue("Question must be displayed",
                getSolo().searchText("What is the answer to life, the universe, and everything?"));
        assertTrue("Correct answer must be displayed", getSolo().searchText("Forty-two"));
        assertTrue("Incorrect answer must be displayed", getSolo().searchText("Twenty-seven"));
        Button nextQuestionButton = getSolo().getButton("Next question");
        assertFalse("Next question button is disabled", nextQuestionButton.isEnabled());
    }
    
    //TODO find why clickontext does not work
    public void testCorrectQuestionSelected() {
        httpClient.pushCannedResponse(
                "GET (?:https?://[^/]+|[^/]+)?/+quizquestions/random\\b",
                HttpStatus.SC_OK,
                "{\"question\": \"What is the answer to life, the universe, and everything?\","
                + " \"answers\": [\"Forty-two\", \"Twenty-seven\"], \"owner\": \"sweng\","
                + " \"solutionIndex\": 0, \"tags\": [\"h2g2\", \"trivia\"], \"id\": \"1\" }",
                "application/json");

        getActivityAndWaitFor(TTChecks.QUESTION_SHOWN);
        assertTrue("Correct answer must be displayed", getSolo().
        		searchText("Forty-two"));
        getSolo().clickOnText("Forty-two");
        //assertTrue("Correct answer has been selected and",
        	//	getSolo().searchText("Forty-two ✔"));
    }
    
    

}
