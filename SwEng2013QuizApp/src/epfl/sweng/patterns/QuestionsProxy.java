package epfl.sweng.patterns;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.apache.http.HttpStatus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CacheOpenHelper;
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
	private SQLiteDatabase mSQLiteWritableCache;

	/**
	 * Returns the singleton, creates it if it's not instantiated.
	 * 
	 * @return Singleton instance of the class.
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
	 * Add a {@link QuizQuestion} to the Inbox only if it is a well formed
	 * question
	 * 
	 * @param question
	 *            The {@link QuizQuestion} to be verify
	 */
	public void addInbox(QuizQuestion question) {
		if (null != question && question.auditErrors() == 0) {
			ContentValues values = new ContentValues(QuizQuestion.FIELDS_COUNT);
			// XXX Sidney possible to change behavior of getTagsToString()?
			values.put("id", question.getId());
			values.put("tags", Arrays.toString(question.getTags().toArray()));
			values.put("statement", question.getQuestionStatement());
			values.put("answers", Arrays.toString(question.getAnswers().toArray()));
			values.put("solutionIndex", question.getSolutionIndex());
			values.put("owner", question.getOwner());
			
			mSQLiteWritableCache.insert(CacheOpenHelper.CACHE_TABLE_NAME,
					null, values);
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
	public QuizQuestion retrieveRandomQuizQuestion() {
		QuizQuestion fetchedQuestion = null;

		if (UserPreferences.getInstance().isConnected()) {
			fetchedQuestion = mNetworkCommunication.retrieveRandomQuizQuestion();
			if (null != fetchedQuestion) {
				addInbox(fetchedQuestion);
			} else {
//				UserPreferences.getInstance()
//					.setConnectivityState(ConnectivityState.OFFLINE);
				fetchedQuestion = extractQuizQuestionFromInbox();
			}
		} else {
			fetchedQuestion = extractQuizQuestionFromInbox();
		}

		return fetchedQuestion;
	}
	
	public List<QuizQuestion> retrieveQuizQuestions(QuizQuery query) {
		
		return null;
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
		
		Cursor randomQuestionCursor = mSQLiteWritableCache.rawQuery(
				"SELECT * FROM " + CacheOpenHelper.CACHE_TABLE_NAME +
				" ORDER BY RANDOM() LIMIT 1;", null);
		randomQuestionCursor.getString(0);
		Log.i("QuestionProxy", "string returned: "+randomQuestionCursor.getString(0));
		randomQuestionCursor.close();
		
		
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
	private QuestionsProxy(Context context) {
		mQuizQuestionsOutbox = new ArrayDeque<QuizQuestion>();
		mQuizQuestionsInbox = new ArrayList<QuizQuestion>();
		mNetworkCommunication = new NetworkCommunication();
		mSQLiteWritableCache = new CacheOpenHelper(context).getWritableDatabase();
	}
	
	private synchronized int sendCachedQuestions() {
		int httpCodeResponse = -1;
		// We first send all the questions that we stored when in
		// offline mode.
		while (mQuizQuestionsOutbox.size() > 0) {

			QuizQuestion questionOut = mQuizQuestionsOutbox.peek();

			httpCodeResponse = mNetworkCommunication.sendQuizQuestion(questionOut);

			if (HttpStatus.SC_CREATED == httpCodeResponse) {
				// If the question has been sent, we remove it from the queue.
				mQuizQuestionsOutbox.remove();
			} else {
				return httpCodeResponse;
			}
		}
		return httpCodeResponse;
	}

	public void closeDB() {
		// here we have to close the db and all unclosed Cursor objects
		mSQLiteWritableCache.close();
	}
}
