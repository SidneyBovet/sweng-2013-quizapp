package epfl.sweng.patterns;

import java.util.List;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import epfl.sweng.agents.QuestionAgent;
import epfl.sweng.agents.QuestionAgentFactory;
import epfl.sweng.backend.Converter;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CacheContentProvider;
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
public final class QuestionsProxy implements ConnectivityProxy,
		INetworkCommunication {

	private static QuestionsProxy sQuestionProxy;
	private QuestionAgent mAgent;

	private INetworkCommunication mNetworkCommunication;

	private Context mContext;
	private CacheContentProvider mContentProvider;
	
	/**
	 * Singleton getter.
	 * 
	 * @return The singleton instance of this object.
	 */
	public static QuestionsProxy getInstance(Context context) {
		// double-checked singleton: avoids calling costly synchronized if unnecessary
		if (null == sQuestionProxy) {
			synchronized (QuestionsProxy.class) {
				if (null == sQuestionProxy) {
					sQuestionProxy = new QuestionsProxy(context);
				}
			}
		}
		return sQuestionProxy;
	}
	
	/**
	 * Singleton getter when no context is available.
	 * 
	 * @return The singleton instance of this object (may be null!)
	 */
	public static QuestionsProxy getInstance() {
		return sQuestionProxy;
	}
	
	/**
	 * Add a {@link QuizQuestion} to the Inbox only if it is a well formed
	 * question
	 * 
	 * @param question
	 *            The {@link QuizQuestion} to be verified
	 */
	public void addInbox(QuizQuestion question) {
		if (null != question && question.auditErrors() == 0) {
			openContentProvider();
			mContentProvider.addQuizQuestion(question);
			closeContentProvider();
		}
	}
	
//	Utility function to send a batch of questions to the cache
//	public void addInbox(Iterable<QuizQuestion> questionBatch) {
//		openContentProvider();
//		
//		for (QuizQuestion quizQuestion : questionBatch) {
//			mContentProvider.addQuizQuestion(quizQuestion);
//		}
//		
//		closeContentProvider();
//	}
	
	/**
	 * Add a {@link QuizQuestion} to the Outbox only if it is a well formed
	 * question
	 * 
	 * @param question
	 *            The {@link QuizQuestion} to be verify
	 */
	public void addOutAndInbox(QuizQuestion question) {
		if (null != question && question.auditErrors() == 0) {
			if (null == mContentProvider || mContentProvider.isClosed()) {
				openContentProvider();
				mContentProvider.addQuizQuestion(question, true);
				closeContentProvider();
			} else {
				mContentProvider.addQuizQuestion(question, true);
			}
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
		// We add the current question to the outbox by default to send it
		// independently of the state we are in (online or offline).
		addOutAndInbox(question);
		
		int httpCodeResponse = -1;
		if (UserPreferences.getInstance().isConnected()) {
			httpCodeResponse = sendCachedQuestions();
		}
		httpCodeResponse = HttpStatus.SC_CREATED;
		return httpCodeResponse;
	}
	
	/**
	 * Retrieves a {@link QuizQuestion} from the server and stores it in the 
	 * cache before returning it if online. Choose a random {@link QuizQuestion}
	 * from the cached content before returning it if offline.
	 * 
	 * @return {@link QuizQuestion} retrieve from the server
	 */
	@Override
	public QuizQuestion retrieveRandomQuizQuestion() {
		QuizQuestion fetchedQuestion = null;
		
		if (UserPreferences.getInstance().isConnected()) {
			fetchedQuestion = mNetworkCommunication.retrieveRandomQuizQuestion();
			if (null != fetchedQuestion) {
				addInbox(fetchedQuestion);
			}
		} else {
			throw new IllegalStateException("retrieveRandomQuizQuestion() " +
					"was called while in offline state");
		}
		
		return fetchedQuestion;
	}
	
	/**
	 * Retrieves a {@link QuizQuestion} from the server according to a specific
	 * query, and stores it in the cache before returning the actual JSON
	 * response.
	 * 
	 * @return {@link JSONObject} containing the list of {@link QuizQuestion}s
	 *         and the next field, if there's any.
	 */
	public JSONObject retrieveQuizQuestions(QuizQuery query) {
		if (UserPreferences.getInstance().getConnectivityState()
				== ConnectivityState.OFFLINE) {
			return null;
		}
		JSONObject jsonResponse = mNetworkCommunication.retrieveQuizQuestions(query);
		
		if (jsonResponse == null) {
			return null;
		}
		
		try {
			JSONArray array = jsonResponse.getJSONArray("questions");
			if (array != null) {
				List<QuizQuestion> fetchedQuestions =
						Converter.jsonArrayToQuizQuestionList(array);
				
				for (QuizQuestion question : fetchedQuestions) {
					addInbox(question);
				}
			}
		} catch (JSONException e) {
			Log.e(this.getClass().getName(), "retrieveQuizQuestions(): could not "
					+ "retrieve the \'questions\' field from the JSON response.");
			return null;
		}
		return jsonResponse;
	}
	
	public int getOutboxSize() {
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
			if (getOutboxSize() > 0) {
				proxyResponse = sendCachedQuestions();
			} else {
				proxyResponse = HttpStatus.SC_CREATED;
			}
		}

		return proxyResponse;
	}

	////////////////////////////////////////////////////////////////////////////
	//////////////// Methods to be used by ShowQuestionActivity ////////////////
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Opens a stream of questions by specifying a query.
	 * @param query
	 */
	public void setStream(QuizQuery query) {
		mAgent = QuestionAgentFactory.getAgent(mContext, query);
	}
	
	/**
	 * @return The next question in the stream defined by the query.
	 */
	// sorry for re-stating what the code does, but its a quite complicated part to me
	public QuizQuestion getNextQuestion() {
		if (null == mAgent || mAgent.isClosed()) {
			throw new IllegalStateException("cannot get next question from " +
					"closed stream (in QuestionProxy).");
		}
		boolean wasConnectedBeforeRetrieving =
				UserPreferences.getInstance(mContext).isConnected();
		
		QuizQuestion fetchedQuestion = mAgent.getNextQuestion();
		
		// if something wrong happened and we were connected
		if (null == fetchedQuestion && wasConnectedBeforeRetrieving) {
			// then if we're still connected: wtf
			if (UserPreferences.getInstance(mContext).isConnected()) {
				Log.wtf(this.getClass().getName(),
						"Server error and not in offline mode?!");
				return null;
			// otherwise
			} else {
				// re-load the agent
				QuizQuery query = mAgent.getQuery();
				mAgent = QuestionAgentFactory.getAgent(mContext, query);
				// re-fetch the question with the new agent
				return getNextQuestion();
			}
		}
		
		return fetchedQuestion;
	}

	/**
	 * Closes the opened stream.
	 */
	public void closeStream() {
		mAgent.close();
		mAgent = null;
	}
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	
	private void openContentProvider() {
		mContentProvider = new CacheContentProvider(mContext, true);
	}
	
	private void closeContentProvider() {
		mContentProvider.close();
		mContentProvider = null;
	}
	
	/**
	 * Private constructor of the singleton.
	 * 
	 */
	private QuestionsProxy(Context ctx) {
		mNetworkCommunication = new NetworkCommunication();
		mContext = ctx;
		mAgent = null;
	}

	private synchronized int sendCachedQuestions() {
		openContentProvider();
		int httpCodeResponse = -1;
		// We first send all the questions that we stored when in
		// offline mode.
		while (getOutboxSize() > 0) {
			QuizQuestion questionOut = mContentProvider.peekFirstQuestionFromOutbox();

			httpCodeResponse = mNetworkCommunication
					.sendQuizQuestion(questionOut);

			if (HttpStatus.SC_CREATED == httpCodeResponse) {
				// If the question has been sent, we remove it from the queue.
				mContentProvider.takeFirstQuestionFromOutbox();
			} else {
				return httpCodeResponse;
			}
		}
		closeContentProvider();
		return httpCodeResponse;
	}

	/**
	 * Has to be refactored, <b>always returns -1!</b>
	 * <br/>
	 * Deprecated since moving to persistent caching
	 * @return -1
	 */
	@Deprecated
	public int getInboxSize() {
		return -1;
	}
}
