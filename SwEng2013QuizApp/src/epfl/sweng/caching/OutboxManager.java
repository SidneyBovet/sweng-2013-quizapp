package epfl.sweng.caching;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import epfl.sweng.app.SwEng2013QuizApp;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * Represents the message outbox when in offline mode.
 * 
 * @author born4new
 * 
 */
public class OutboxManager {

	private CacheContentProvider mCache = null;
	private SQLiteDatabase mDatabase = null;

	public OutboxManager() {
		mCache = new CacheContentProvider(true);

		try {
			SQLiteCacheHelper openHelper = new SQLiteCacheHelper(
					SwEng2013QuizApp.getContext());
			mDatabase = openHelper.getWritableDatabase();
		} catch (SQLiteException e) {
			Log.e("DB", "Could not open the DB.", e);
		}
	}

	/**
	 * Adds an already existing {@link QuizQuestion} to the outbox.
	 * 
	 * @param id
	 *            The id of the question to be added to the outbox.
	 * @return the PK of the added question.
	 */
	public long push(long id) {
		return changeQuestionOutboxStatus(id, true);
	}

	/**
	 * Removes and returns the first question in the outbox.
	 * 
	 * @return First question in the outbox.
	 */
	public QuizQuestion pop() {

		QuizQuestion returnedQuestion = null;
		long id = getIdOfFirstQuestion();

		if (-1 != id) {
			returnedQuestion = mCache.getQuestionFromPK(id);
			if (null != returnedQuestion) {
				changeQuestionOutboxStatus(id, false);
			}
		}

		return returnedQuestion;
	}

	/**
	 * Returns a copy of the first question in the outbox.
	 * 
	 * @return the first question in the outbox.
	 */
	public QuizQuestion peek() {
		return mCache.getQuestionFromPK(getIdOfFirstQuestion());
	}

	/**
	 * Returns the number of questions currently in the outbox cache.
	 * 
	 * @return the number of questions currently in the outbox cache.
	 */
	public int size() {
		sanityDatabaseCheck();

		return (int) DatabaseUtils.queryNumEntries(mDatabase,
				SQLiteCacheHelper.TABLE_QUESTIONS,
				SQLiteCacheHelper.FIELD_QUESTIONS_IS_QUEUED + "=1");
	}

	public void close() {
		sanityDatabaseCheck();
		mCache.close();
		mDatabase.close();
	}

	/**
	 * Tell if the outboxManager is closed.
	 * 
	 * @return true if closed, false otherwise.
	 */
	public boolean isClosed() {
		return (null == mDatabase || !mDatabase.isOpen()) && mCache.isClosed();
	}

	/*********************** Private methods ***********************/

	/**
	 * Get the first question ID in the outbox stack.
	 * 
	 * @return the first question ID.
	 */
	private long getIdOfFirstQuestion() {

		long id = -1;

		Cursor questionOutboxIdCursor = mDatabase.query(
				SQLiteCacheHelper.TABLE_QUESTIONS,
				new String[] {SQLiteCacheHelper.FIELD_QUESTIONS_PK},
				SQLiteCacheHelper.FIELD_QUESTIONS_IS_QUEUED + "=1", null, null,
				null, SQLiteCacheHelper.FIELD_QUESTIONS_PK + " ASC", "1");

		if (questionOutboxIdCursor.moveToFirst()) {
			id = questionOutboxIdCursor.getLong(questionOutboxIdCursor
					.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_PK));

			questionOutboxIdCursor.close();
		}

		return id;
	}

	/**
	 * Changes the question with id given in parameter to outbox or cache given
	 * the boolean in parameter.
	 * 
	 * @param id
	 *            question id.
	 * @param questionInOutbox
	 *            do you want the question to be in the outbox (true) or the
	 *            cache (false)?
	 */
	private long changeQuestionOutboxStatus(long id, boolean questionInOutbox) {
		ContentValues isQueuedValue = new ContentValues(1);
		isQueuedValue.put(SQLiteCacheHelper.FIELD_QUESTIONS_IS_QUEUED,
				questionInOutbox ? 1 : 0);
		mDatabase.update(SQLiteCacheHelper.TABLE_QUESTIONS, isQueuedValue,
				SQLiteCacheHelper.FIELD_QUESTIONS_PK + "=?",
				new String[] {String.valueOf(id)});

		return id;
	}

	/**
	 * Check whether the cache is accessible.
	 * 
	 * @throws IllegalStateException
	 *             If the cache is unavailable.
	 */
	private void sanityDatabaseCheck() {
		if (null == mDatabase || !mDatabase.isOpen()) {
			throw new IllegalStateException(
					"The database object is either null or closed");
		}
	}
}
