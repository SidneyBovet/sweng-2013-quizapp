package epfl.sweng.patterns;

import java.util.ArrayList;
import java.util.List;
import epfl.sweng.exceptions.ServerSubmitFailedException;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.ServerInteractions;

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
public final class QuestionsProxy {
	
	private static QuestionsProxy sQuestionProxy;
	private List<QuizQuestion> mQuizzQuestionsOutbox;
	private List<QuizQuestion> mQuizzQuestionsInbox;
	
	/**
	 * Private constructor of the singleton.
	 * 
	 */
	
	private QuestionsProxy() {
		mQuizzQuestionsOutbox = new ArrayList<QuizQuestion>();
		mQuizzQuestionsInbox = new ArrayList<QuizQuestion>();
	}

	/**
	 * Returns the singleton, creates it if it's not instancied.
	 * 
	 * @return Singleton instance of the class.
	 */
	
	public static QuestionsProxy getInstance() {
		// double-checked singleton: avoids calling costly synchronized if
		// unnecessary
		if (null == sQuestionProxy) {
			synchronized (QuestionsProxy.class) {
				if (null == sQuestionProxy) {
					sQuestionProxy = new QuestionsProxy();
				}
			}
		}
		return sQuestionProxy;
	}

	
	public void sendQuizzQuestion(QuizQuestion question) {
		//ONLINE
		
		mQuizzQuestionsInbox.add(question);
		
		try {
			ServerInteractions.submitQuestion(question);
			/*if (httpResponse == HttpStatus.SC_CREATED) {
				// TODO In general, we should put error messages in strings.xml
				// and especially make a hierachy if possible.
				// Toast.makeText(this, "Quiz submitted to the server.",
						//Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this,
						"Could not upload the question to the server",
						Toast.LENGTH_SHORT).show();
			}*/
		} catch (ServerSubmitFailedException e) {
			/*// TODO Log it? (Since we did it on the two layers before,
			// I'm wondering if we should do it here) Problem with the server
			Log.e(this.getClass().getName(), "sendEditedQuestion(): The "
					+ "question could not be submitted.", e);
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();*/
		}
		
	}
	
	public QuizQuestion retrieveQuizzQuestion() {
		
		// Online
		
		// fetching question
		QuizQuestion fetchedQuestion = ServerInteractions.getRandomQuestion();
		/*if (null == randomQuestion) {
			Log.i(this.getClass().getName(), "Fetching a random question failed");
			Toast.makeText(this, R.string.error_fetching_question,
					Toast.LENGTH_LONG).show();
			TestCoordinator.check(TTChecks.QUESTION_SHOWN);
			//finish();
			return;
		}*/
		
		// ...stores it inside the inbox...
		mQuizzQuestionsInbox.add(fetchedQuestion);
		
		return fetchedQuestion;
		//...and send it back to the user.
	}
}
