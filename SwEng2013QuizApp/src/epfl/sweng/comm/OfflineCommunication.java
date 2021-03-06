package epfl.sweng.comm;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.caching.OutboxManager;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * Offline Communication, with a content provider and a SQL Database.
 * 
 * @author Melody Lucid
 * 
 */
public class OfflineCommunication implements IQuestionCommunication {

	private QuizQuery mCurrentQueryServed;
	private CacheContentProvider mContentProvider;
	private OutboxManager mOutbox;
	private Cursor mCursor;

	public OfflineCommunication() {
		mContentProvider = new CacheContentProvider(false);
		mOutbox = new OutboxManager();
	}

	/**
	 * Caches a {@link QuizQuestion}.
	 */
	@Override
	public int sendQuizQuestion(QuizQuestion quizQuestion) {
		addOutbox(quizQuestion);
		return HttpStatus.SC_CREATED;
	}

	/**
	 * Retrieves a {@link QuizQuestion} from the cache, according to a specific
	 * {@link QuizQuery}.
	 */
	@Override
	public JSONObject retrieveQuizQuestion(QuizQuery quizQuery) {
		if (mCurrentQueryServed == null) {
			mCurrentQueryServed = quizQuery;
		}
		
		if (!mCurrentQueryServed.equals(quizQuery) || mCursor == null) {
			mCursor = mContentProvider.getQuestions(quizQuery);
		}

		QuizQuestion retrievedQuestion = null;

		if (mCursor.getCount() == 0 || mCursor.isAfterLast()) {
			return null;
		}

		int questionPK = mCursor.getInt(mCursor.getColumnIndex("id"));
		retrievedQuestion = mContentProvider.getQuestionFromPK(questionPK);

		boolean couldMove = mCursor.moveToNext();
		if (!couldMove) {
			mCursor.moveToFirst();
		}

		JSONObject jsonQuestions = new JSONObject();
		try {
			JSONArray array = new JSONArray();
			JSONObject jsonQuestion = retrievedQuestion.toJSON();
			if (jsonQuestion == null) {
				return null;
			}
			array.put(jsonQuestion);

			jsonQuestions.put("questions", (Object) array); // Ouch! TODO

			// TODO make "next" tag follow the position of the cursor for double
			// check
			jsonQuestions.put("next", couldMove ? "Yup there's more." : null);
		} catch (JSONException e) {
			Log.e(this.getClass().getName(),
					"Unable to create JSON containing " + "the questions.", e);
		}
		return jsonQuestions;
	}

	/**
	 * Retrieves a random {@link QuizQuestion} from the cache.
	 */
	@Override
	public JSONObject retrieveRandomQuizQuestion() {
		if (mCursor == null || mCurrentQueryServed != null) {
			mCurrentQueryServed = null;
			mCursor = mContentProvider.getQuestions(new QuizQuery());
		}

		QuizQuestion retrievedQuestion = null;

		if (mCursor.getCount() == 0 || mCursor.isAfterLast()) {
			return null;
		}

		int questionPK = mCursor.getInt(mCursor.getColumnIndex("id"));
		retrievedQuestion = mContentProvider.getQuestionFromPK(questionPK);

		if (!mCursor.moveToNext()) {
			mCursor.moveToFirst();
		}

		return retrievedQuestion.toJSON();
	}
	
	/**
	 * Closes everything related to the cache content provider.
	 */
	@Override
	public void close() {
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}
	}

	/**
	 * Add a {@link QuizQuestion} to the Outbox only if it is a well formed
	 * question.
	 * <p>
	 * <b>Note</b>: this also adds the question to the Inbox.
	 * 
	 * @param question
	 *            The {@link QuizQuestion} to be verify
	 */

	private void addOutbox(QuizQuestion quizQuestion) {
		if (null != quizQuestion && quizQuestion.auditErrors() == 0) {
			long id = mContentProvider.addQuizQuestion(quizQuestion);
			mOutbox.push(id);
		}
	}

}
