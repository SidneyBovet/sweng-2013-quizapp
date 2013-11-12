package epfl.sweng.patterns;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.apache.http.HttpStatus;

import android.util.Log;
import epfl.sweng.preferences.UserPreferences;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.INetworkCommunication;
import epfl.sweng.servercomm.NetworkCommunication;

/**
 * This class will perform all the server interactions in the place of our app.
 * It will also cache all the questions that we fetch from the server and take
 * place of the server when in offline mode.
 * 
 * @author born4new, JoTearoom, Merok
 * 
 */
public final class QuestionsProxy 
	implements ConnectivityProxy, INetworkCommunication {

	private static QuestionsProxy sQuestionProxy;
	// question to be sent
	private Queue<QuizQuestion> mQuizQuestionsOutbox;
	// question to be retrieve
	private List<QuizQuestion> mQuizQuestionsInbox;
		
	private INetworkCommunication mNetworkCommunication;

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

	/**
	 * Add a {@link QuizQuestion} to the Inbox only if it is a well formed
	 * question
	 * 
	 * @param question
	 *            The {@link QuizQuestion} to be verify
	 */
	public void addInbox(QuizQuestion question) {
		if (null != question && question.auditErrors() == 0) {
			mQuizQuestionsInbox.add(question);
		}
	}

	/**
	 * Add a {@link QuizQuestion} to the Outbox only if it is a well formed
	 * question
	 * 
	 * @param question
	 *            The {@link QuizQuestion} to be verify
	 */
	public void addOutbox(QuizQuestion question) {
		if (null != question && question.auditErrors() == 0) {
			mQuizQuestionsOutbox.add(question);
		}
	}

	/**
	 * Send a {@link QuizQuestion} to the server after having stored it in the
	 * cache and send the cached questions to be sent if online. Store the
	 * {@link QuizQuestion} to be sent in the cache if offline.
	 * 
	 * @param question
	 *            {@link QuizQuestion} that we want to send
	 */
	public int sendQuizQuestion(QuizQuestion question) {

		// We add in the inbox to make this question accessible in offline mode.
		addInbox(question);
		// We add the current question to the outbox by default to send it
		// independently of the state we are in (online or offline).
		addOutbox(question);

		int httpCodeResponse = -1;
		if (UserPreferences.getInstance().isConnected()) {
			httpCodeResponse = sendCachedQuestions();
		}
		httpCodeResponse = HttpStatus.SC_CREATED;
		return httpCodeResponse;
	}

	/**
	 * Retrieve a {@link QuizQuestion} from the server and store it in the cache
	 * before returning it if online. Choose a random {@link QuizQuestion} from
	 * the cached content before returning it if offline.
	 * 
	 * @return {@link QuizQuestion} retrieve from the server
	 */
	@Override
	public QuizQuestion retrieveQuizQuestion() {
		QuizQuestion fetchedQuestion = null;

		if (UserPreferences.getInstance().isConnected()) {
			fetchedQuestion = mNetworkCommunication.retrieveQuizQuestion();
			if (null != fetchedQuestion) {
				addInbox(fetchedQuestion);
			} else {
				UserPreferences.getInstance()
					.setConnectivityState(ConnectivityState.OFFLINE);
				fetchedQuestion = extractQuizQuestionFromInbox();
			}
		} else {
			fetchedQuestion = extractQuizQuestionFromInbox();
		}

		return fetchedQuestion;
	}

	public int getOutboxSize() {
		return mQuizQuestionsOutbox.size();
	}

	public int getInboxSize() {
		return mQuizQuestionsInbox.size();
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
			proxyResponse = HttpStatus.SC_OK; // Nothing to do
		} else if (newState == ConnectivityState.ONLINE) {
			if (mQuizQuestionsOutbox.size() > 0) {
				proxyResponse = sendCachedQuestions();
			} else {
				proxyResponse = HttpStatus.SC_CREATED;
			}
		}
		
		return proxyResponse;
	}

	/**
	 * Extracts a {@link QuizQuestion} from the Inbox, returning null if empty
	 * 
	 * @return The extracted question, null if empty.
	 */
	private QuizQuestion extractQuizQuestionFromInbox() {
		QuizQuestion extractedQuestion = null;
		if (mQuizQuestionsInbox.size() > 0) {
			int questionIDCache = new Random()
					.nextInt(mQuizQuestionsInbox.size());
			extractedQuestion = mQuizQuestionsInbox.get(questionIDCache);
		} else {
			Log.i("QuestionProxy", "Inbox empty!");
			extractedQuestion = null;
		}
		return extractedQuestion;
	}

	/**
	 * Private constructor of the singleton.
	 * 
	 */
	private QuestionsProxy() {
		mQuizQuestionsOutbox = new ArrayDeque<QuizQuestion>();
		mQuizQuestionsInbox = new ArrayList<QuizQuestion>();
		mNetworkCommunication = new NetworkCommunication();
	}
	
	private synchronized int sendCachedQuestions() {
		int responseStatus = -1;
		// We first send all the questions that we stored when in
		// offline mode.
		while (mQuizQuestionsOutbox.size() > 0) {

			QuizQuestion questionOut = mQuizQuestionsOutbox.peek();

			responseStatus = mNetworkCommunication.sendQuizQuestion(questionOut);

			if (HttpStatus.SC_CREATED == responseStatus) {
				// If the question has been sent, we remove it from the queue.
				mQuizQuestionsOutbox.remove();
			} else {
				UserPreferences.getInstance()
					.setConnectivityState(ConnectivityState.OFFLINE);
				return responseStatus;
			}
		}
		return responseStatus;

	}
}
