package epfl.sweng.caching;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * Object providing interface to local questions database.
 * 
 * @author Sidney
 *
 */
public class CacheContentProvider {
	
	private SQLiteDatabase mDatabase = null;

	public CacheContentProvider(Context context, boolean writable) {
		CacheOpenHelper openHelper = new CacheOpenHelper(context);
		if (writable) {
			mDatabase = openHelper.getReadableDatabase();
		} else {
			mDatabase = openHelper.getWritableDatabase();
		}
	}
	
	/**
	 * 
	 * @return An unique and randomly retrieved question.
	 */
	public QuizQuestion getRandomQuestion() {
		sanityDatabaseCheck();
		return null;
	}
	
	/**
	 * 
	 * @param querry
	 * 			The query describing the set of questions that will be returned.
	 * @return
	 * 			A {@link Cursor} pointing to the set of queried questions.
	 */
	public Cursor getQuestions(QuizQuery querry) {
		sanityDatabaseCheck();
		return null;
	}
	
	/**
	 * 
	 * @param questionToAdd The question to be added to the cache
	 */
	public void addQuizQuestion(QuizQuestion questionToAdd) {
		sanityDatabaseCheck();
		
	}
	
	public void destroy() {
		sanityDatabaseCheck();
		mDatabase.close();
	}
	
	private void sanityDatabaseCheck() {
		// note that use of isDatabaseIntegrityOk() may take a long time,
		// it is therefore avoided here.
		if (null == mDatabase || !mDatabase.isOpen()) {
			throw new IllegalStateException(
					"The database object is either null or closed");
		}
	}
}
