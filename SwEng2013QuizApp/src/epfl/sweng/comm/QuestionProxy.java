package epfl.sweng.comm;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * This class will perform all the interactions with the communication services
 * in the place of our application.
 * <p>
 * It can switch the communication services from ONLINE to OFFLINE. When passing
 * ONLINE, it will send the cached questions.
 * 
 * @author born4new, JoTearoom, Merok, Melody Lucid
 * 
 */
public final class QuestionProxy implements QuestionCommunication,
		ConnectivityProxy {

	private static QuestionProxy sSingletonProxy;
	private QuestionCommunication mActualCommunication;
	private CacheContentProvider mContentProvider;

	/**
	 * Singleton getter.
	 * 
	 * @return The singleton instance of this object.
	 */
	
	public static QuestionProxy getInstance() {
		// double-checked singleton: avoids calling costly synchronized if
		// unnecessary
		if (null == sSingletonProxy) {
			synchronized (QuestionProxy.class) {
				if (null == sSingletonProxy) {
					sSingletonProxy = new QuestionProxy();
				}
			}
		}
		return sSingletonProxy;
	}
	
	/**
	 * Resets the instance of the singleton to <code>null</code>.
	 */
	
	public static void resetQuestionsProxy() {
		sSingletonProxy = null;
	}

	/**
	 * Sends a {@link QuizQuestion} to the actual communication service.
	 * 
	 * @param question
	 *            {@link QuizQuestion} that we want to send
	 * @return The HTTP status code in response of the request.
	 */

	@Override
	public int sendQuizQuestion(QuizQuestion quizQuestion) {
		int httpCodeResponse 
			= mActualCommunication.sendQuizQuestion(quizQuestion);

		return httpCodeResponse;
	}

	/**
	 * Retrieves a {@link QuizQuestion} from the actual communication service,
	 * according to a specific query.
	 * <p>
	 * Choose a random {@link QuizQuestion} from the cached content before
	 * returning it if offline.
	 */
	
	@Override
	public JSONObject retrieveQuizQuestion(QuizQuery quizQuery) {
		JSONObject jsonRetrievedQuestions
			= mActualCommunication.retrieveQuizQuestion(quizQuery);
		
		return jsonRetrievedQuestions;
	}

	/**
	 * Retrieves a random {@link QuizQuestion} from the actual communication
	 * service.
	 */
	
	@Override
	public JSONObject retrieveRandomQuizQuestion() {
		JSONObject jsonRetrievedQuestion 
			= mActualCommunication.retrieveRandomQuizQuestion();
		
		return jsonRetrievedQuestion;
	}

	/**
	 * Notifies the proxy of a new connectivity state. The proxy will change to
	 * the new state, send the according request, and return their corresponding
	 * HTTP response code.
	 * 
	 * @return The HTTP response code of the last proxy request.
	 */
	
	@Override
	public int notifyConnectivityChange(ConnectivityState newState) {
		int proxyResponse = -1;

		if (newState == ConnectivityState.OFFLINE) {
			proxyResponse = HttpStatus.SC_OK;
			mActualCommunication = new OfflineCommunication();
		} else if (newState == ConnectivityState.ONLINE) {
			openContentProvider();
			if (getOutboxSize() > 0) {
				proxyResponse = sendCachedQuestions();
			} else {
				proxyResponse = HttpStatus.SC_CREATED;
				mActualCommunication = new OnlineCommunication();
			}
			closeContentProvider();
		}

		return proxyResponse;
	}
	
	private int getOutboxSize() {
		int count = -1;
		if (null == mContentProvider || mContentProvider.isClosed()) {
			openContentProvider();
			count = mContentProvider.getOutboxCount();
			closeContentProvider();
		} else {
			count = mContentProvider.getOutboxCount();
		}
		return count;
	}
	
	private synchronized int sendCachedQuestions() {
		mActualCommunication = new OnlineCommunication();
		int httpCodeResponse = -1;
		// We first send all the questions that we stored when in
		// offline mode.
		while (mContentProvider.getOutboxCount() > 0) {
			QuizQuestion questionOut 
				= mContentProvider.peekFirstQuestionFromOutbox();
			
			httpCodeResponse 
				= mActualCommunication.sendQuizQuestion(questionOut);
			
			if (HttpStatus.SC_CREATED == httpCodeResponse) {
				// If the question has been sent, we remove it from the queue.
				mContentProvider.takeFirstQuestionFromOutbox();
			} else {
				return httpCodeResponse;
			}
		}
		return httpCodeResponse;
	}
	
	private void openContentProvider() {
		mContentProvider = new CacheContentProvider(true);
	}

	private void closeContentProvider() {
		mContentProvider.close();
		mContentProvider = null;
	}

	private QuestionProxy() {
		mActualCommunication = new OnlineCommunication();
	}
}
