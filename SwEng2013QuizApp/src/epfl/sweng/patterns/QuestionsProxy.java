//package epfl.sweng.patterns;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import epfl.sweng.authentication.UserPreferences;
//import epfl.sweng.exceptions.ServerSubmitFailedException;
//import epfl.sweng.quizquestions.QuizQuestion;
//import epfl.sweng.servercomm.ServerInteractions;
//
///**
// * This class will perform all the server interactions
// * in the place of our app. It will also cache all the 
// * questions that we fetch from the server and take place
// * of the server when in offline mode.
// * 
// * XXX If we don't use a Singleton, how do we ensure that
// * we only have one proxy class? Do we have a boolean flag
// * that tells us if it has been instanciated?
// *  
// *  
// * @author born4new, JoTearoom, Merok
// *
// */
//public final class QuestionsProxy {
//	
//	private static QuestionsProxy sQuestionProxy;
//	//question to be sent
//	private List<QuizQuestion> mQuizzQuestionsOutbox;
//	//question to be retrieve
//	private List<QuizQuestion> mQuizzQuestionsInbox;
//	private UserPreferences mUserPreferences;
//	
//	/**
//	 * Private constructor of the singleton.
//	 * 
//	 */
//	private QuestionsProxy() {
//		mQuizzQuestionsOutbox = new ArrayList<QuizQuestion>();
//		mQuizzQuestionsInbox = new ArrayList<QuizQuestion>();
//		mUserPreferences = UserPreferences.getInstance();
//	}
//
//	/**
//	 * Returns the singleton, creates it if it's not instancied.
//	 * 
//	 * @return Singleton instance of the class.
//	 */
//	
//	public static QuestionsProxy getInstance() {
//		// double-checked singleton: avoids calling costly synchronized if
//		// unnecessary
//		if (null == sQuestionProxy) {
//			synchronized (QuestionsProxy.class) {
//				if (null == sQuestionProxy) {
//					sQuestionProxy = new QuestionsProxy();
//				}
//			}
//		}
//		return sQuestionProxy;
//	}
//
//	/**
//	 * Add a {@link QuizQuestion} to the Inbox only if it is a well 
//	 * formed question
//	 * @param question The {@link QuizQuestion} to be verify
//	 */
//	public void addInbox(QuizQuestion question) {
//		if (question.auditErrors() == 0) {
//			mQuizzQuestionsInbox.add(question);
//		}
//	}
//	
//	/**
//	 * Add a {@link QuizQuestion} to the Outbox only if it is a well 
//	 * formed question
//	 * @param question The {@link QuizQuestion} to be verify
//	 */
//	public void addOutbox(QuizQuestion question) {
//		if (question.auditErrors() == 0) {
//			mQuizzQuestionsOutbox.add(question);
//		}
//	}
//	
//	/**
//	 * Send a {@link QuizQuestion} to the server after having stored 
//	 * it in the cache and send the cached questions to be sent if online.
//	 * Store the {@link QuizQuestion} to be sent in the cache if offline.
//	 * @param question {@link QuizQuestion} that we want to send
//	 */
//	public void sendQuizzQuestion(QuizQuestion question) {
//		
//		addInbox(question);
//		
//		try {
//			if (mUserPreferences.isConnected()) {
//				/*while (mQuizzQuestionsOutbox.size() > 0) {
//					QuizQuestion mquestionOut = mQuizzQuestionsOutbox.remove(0);
//					ServerInteractions.submitQuestion(mquestionOut);
//					//XXX pour l'instant pas dans le bon ordre => regler asynctask
//				}*/
//				ServerInteractions.submitQuestion(question);
//				//XXX repris de EditQuestionActivity => à mettre à un bon endroit
//				/*if (httpResponse == HttpStatus.SC_CREATED) {
//					// TODO In general, we should put error messages in strings.xml
//					// and especially make a hierachy if possible.
//					// Toast.makeText(this, "Quiz submitted to the server.",
//							//Toast.LENGTH_SHORT).show();
//				} else {
//					Toast.makeText(this,
//							"Could not upload the question to the server",
//							Toast.LENGTH_SHORT).show();
//				}*/
//	
//			} else {
//				addOutbox(question);
//			}
//		} catch (ServerSubmitFailedException e) {
//			//XXX repris de EditQuestionActivity => à mettre à un bon endroit
//			/*// TODO Log it? (Since we did it on the two layers before,
//			// I'm wondering if we should do it here) Problem with the server
//			Log.e(this.getClass().getName(), "sendEditedQuestion(): The "
//					+ "question could not be submitted.", e);
//			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();*/
//		}
//		
//	}
//	
//	/**
//	 * Retrieve a {@link QuizQuestion} from the server and store it in the 
//	 * cache before returning it if online.
//	 * Choose a random {@link QuizQuestion} from the cached content before
//	 * returning it if offline.
//	 * @return {@link QuizQuestion} retrieve from the server 
//	 */
//	public QuizQuestion retrieveQuizzQuestion() {
//		QuizQuestion fetchedQuestion = null;
//		
//		if (mUserPreferences.isConnected()) {
//			fetchedQuestion = ServerInteractions.getRandomQuestion();
//			//XXX repris de ShowQuestionActivity => à mettre à un bon endroit
//			/*if (null == randomQuestion) {
//				Log.i(this.getClass().getName(), "Fetching a random question failed");
//				Toast.makeText(this, R.string.error_fetching_question,
//						Toast.LENGTH_LONG).show();
//				TestCoordinator.check(TTChecks.QUESTION_SHOWN);
//				//finish();
//				return;
//			}*/
//			addInbox(fetchedQuestion);
//		} else {
//			//TODO gerer cas on commence en mode offline donc liste vide
//			int questionIDCache = new Random()
//				.nextInt(mQuizzQuestionsInbox.size());
//			fetchedQuestion = mQuizzQuestionsInbox.get(questionIDCache);
//		}
//		
//		return fetchedQuestion;
//	}
//}
