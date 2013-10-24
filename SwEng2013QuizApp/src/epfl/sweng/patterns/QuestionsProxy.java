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
 * XXX If we don't use a Singleton, how do we ensure that
 * we only have one proxy class? Do we have a boolean flag
 * that tells us if it has been instanciated?
 *  
 *  
 * @author born4new
 *
 */
public class QuestionsProxy {
	
	private List<QuizQuestion> mQuizzQuestionsOutbox;
	private List<QuizQuestion> mQuizzQuestionsInbox;
	
	public QuestionsProxy() {
		mQuizzQuestionsOutbox = new ArrayList<QuizQuestion>();
		mQuizzQuestionsInbox = new ArrayList<QuizQuestion>();
	}
	
	// XXX Do we store questions when in online mode, so that
	// we can also propose the question to the user?
	public void sendQuizzQuestion(QuizQuestion question) {
		// Store question if in offline mode.
		mQuizzQuestionsOutbox.add(question);
	}
	
	public void retrieveQuizzQuestion() {
		
		// Online
		
		// Fetches the question from the server...
		QuizQuestion fetchedQuestion = null;
		
		// ...stores it inside the inbox...
		mQuizzQuestionsInbox.add(fetchedQuestion);
		
		//...and send it back to the user.
	}
}
