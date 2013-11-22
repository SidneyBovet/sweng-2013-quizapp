package epfl.sweng.caching;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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
		SQLiteCacheHelper openHelper = new SQLiteCacheHelper(context);
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
		QuizQuestion extractedQuestion = null;

		Cursor randomQuestionCursor = mDatabase.rawQuery(
				"SELECT * FROM " + SQLiteCacheHelper.TABLE_QUESTIONS +
				" ORDER BY RANDOM() LIMIT 1;", null);
		randomQuestionCursor.getString(0);
		Log.i("QuestionProxy", "string returned: "+randomQuestionCursor.getString(0));
		randomQuestionCursor.close();
		
		
		return extractedQuestion;
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

		ContentValues values = new ContentValues(QuizQuestion.FIELDS_COUNT);
		// XXX Sidney possible to change behavior of getTagsToString()?
		values.put("id", questionToAdd.getId());
		values.put("tags", Arrays.toString(questionToAdd.getTags().toArray()));
		values.put("statement", questionToAdd.getQuestionStatement());
		values.put("answers", Arrays.toString(questionToAdd.getAnswers().toArray()));
		values.put("solutionIndex", questionToAdd.getSolutionIndex());
		values.put("owner", questionToAdd.getOwner());
		
		mDatabase.insert(SQLiteCacheHelper.TABLE_QUESTIONS,
				null, values);
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
