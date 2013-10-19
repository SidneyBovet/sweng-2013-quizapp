package epfl.sweng.test.servercomm;

import java.util.ArrayList;
import java.util.List;

import epfl.sweng.exceptions.ServerSubmitFailedException;
import epfl.sweng.servercomm.ServerInteractions;
import junit.framework.TestCase;

public class ServerInteractionsTest extends TestCase {
	
	@Override
	public void setUp() {
		
	}
	
	@Override
	public void tearDown() {
		
	}
	
	public void testGetRandomQuestion() {
		assertTrue(ServerInteractions.getRandomQuestion() != null);
	}
	
	public void testSubmitQuestion() {
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
		
		try {
			int serverReturn = ServerInteractions.submitQuestion(listInputGUI);
			// XXX This was 400 at the time of writing.
			assertEquals(serverReturn, 201);
		} catch (ServerSubmitFailedException e) {
			fail("An exception occured while doing the test.");
		}
	}
}
