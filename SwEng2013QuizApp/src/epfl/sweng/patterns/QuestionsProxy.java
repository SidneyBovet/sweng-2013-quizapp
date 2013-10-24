package epfl.sweng.patterns;

import java.util.ArrayList;
import java.util.List;

import epfl.sweng.quizquestions.QuizQuestion;

/**
 * This class will perform all the server interactions
 * in the place of our app. It will also cache all the 
 * questions that we fetch from the server and take place
 * of the server when in offline mode.
 * 
 *  
 * @author born4new
 *
 */
public class QuestionsProxy {
	
	private List<QuizQuestion> quizzQuestionsOutbox;
	private List<QuizQuestion> quizzQuestionsInbox;
	
	public QuestionsProxy() {
		quizzQuestionsOutbox = new ArrayList<QuizQuestion>();
		quizzQuestionsInbox = new ArrayList<QuizQuestion>();
	}
	
	public void sendQuizzQuestion(QuizQuestion question) {
		// Store question if in offline mode.
		quizzQuestionsOutbox.add(null);
	}
	
	public void retrieveAndCacheQuizzQuestion() {
		
		// Online
		
		// Fetches the question from the server...
		QuizQuestion fetchedQuestion = null;
		
		// ...stores it inside the inbox...
		quizzQuestionsInbox.add(fetchedQuestion);
		
		//...and send it back to the user.
	}
}
