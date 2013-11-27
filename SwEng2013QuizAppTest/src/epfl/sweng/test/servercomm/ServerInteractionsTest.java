package epfl.sweng.test.servercomm;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.INetworkCommunication;
import epfl.sweng.servercomm.NetworkCommunication;
import epfl.sweng.servercomm.SwengHttpClientFactory;
import epfl.sweng.test.minimalmock.MockHttpClient;

public class ServerInteractionsTest extends TestCase {
	private MockHttpClient mMockClient;
	private INetworkCommunication mNetworkCommunication = new NetworkCommunication();

	@Override
	public void setUp() {
		mMockClient = new MockHttpClient();
		mMockClient
				.pushCannedResponse(
						".+",
						200,
						"{\"id\": 17005,\"question\": \"What is the capital of Antigua and Barbuda?\",\"answers\": [\"Chisinau\"],\"solutionIndex\": 2,\"tags\": [\"capitals\"],\"owner\": \"sweng\"}",
						"application/json");
		SwengHttpClientFactory.setInstance(mMockClient);
	}

	@Override
	public void tearDown() {

	}

	public void testGetRandomQuestion() {

		assertTrue(mNetworkCommunication.retrieveRandomQuizQuestion() != null);
	}

	public void testSubmitQuestion() {
		mMockClient.pushCannedResponse(
				"(POST|GET) https://sweng-quiz.appspot.com/quizquestions/",
				200, "", "HttpResponse");
		List<String> listInputGUI = new ArrayList<String>();

		listInputGUI.add("Question");

		List<String> listAnswers = new ArrayList<String>();
		listAnswers.add("Answer1");
		listAnswers.add("Answer2");

		listInputGUI.addAll(listAnswers);

		int indexGoodAnswer = 1;
		String indexGoodAnswerString = Integer.toString(indexGoodAnswer);

		listInputGUI.add(indexGoodAnswerString);
		listInputGUI.add("tag1, tag2, tag3");
		QuizQuestion questionToSubmit = QuizQuestion
				.createQuestionFromList(listInputGUI);
		int serverReturn = mNetworkCommunication.
				sendQuizQuestion(questionToSubmit);
		assertEquals(serverReturn, 200);
	}

	public void testSubmitQuestionBadQuestion() {
		mMockClient.pushCannedResponse(
				"(POST|GET) https://sweng-quiz.appspot.com/quizquestions/",
				400, "", "HttpResponse");
		List<String> listInputGUI = new ArrayList<String>();

		listInputGUI.add("Question");

		List<String> listAnswers = new ArrayList<String>();
		listAnswers.add("   ");
		listAnswers.add("Answer2");

		listInputGUI.addAll(listAnswers);

		int indexGoodAnswer = 1;
		String indexGoodAnswerString = Integer.toString(indexGoodAnswer);

		listInputGUI.add(indexGoodAnswerString);
		listInputGUI.add("tag1, tag2, tag3");
		QuizQuestion questionToSubmit = QuizQuestion
				.createQuestionFromList(listInputGUI);
		int serverReturn = mNetworkCommunication.
				sendQuizQuestion(questionToSubmit);
		assertEquals(serverReturn, 400);
	}
}
