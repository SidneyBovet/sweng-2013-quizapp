package epfl.sweng.caching;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
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
	
	private static final int OWNER_COLUMN = 5;
	private static final int SOLUTION_INDEX_COLUMN = 4;
	private static final int ANSWER_COLUMN = 3;
	private static final int STATEMENT_COLUMN = 2;
	private static final int ID_COLUMN = 0;
	private static final int TAGS_COLUMN = 1;
	private SQLiteDatabase mDatabase = null;

	/**
	 * A content provider for the persistent cache.<br/>
	 * <i><b>NOTE:</b> It must be closed via its <code>destroy()</code>
	 * method!</i>
	 * @param context The activity's context
	 * @param writable Indicates whether this content provider is read-only.
	 */
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
		randomQuestionCursor.moveToFirst();
		extractedQuestion = extractQuestionFromCursor(randomQuestionCursor);
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
	 * Adds a {@link QuizQuestion}to the persistent cache.
	 * 
	 * @param questionToAdd The question to be added to the cache
	 */
	public void addQuizQuestion(QuizQuestion questionToAdd) {
		sanityDatabaseCheck();

		ContentValues values = new ContentValues(QuizQuestion.FIELDS_COUNT);
		values.put("id", questionToAdd.getId());
		// TODO /!\ Tags are currently dirtily put as a big string => to be changed 
		values.put("tags", Arrays.toString(questionToAdd.getTags().toArray()));
		values.put("statement", questionToAdd.getStatement());
		values.put("answers", Arrays.toString(questionToAdd.getAnswers().toArray()));
		values.put("solutionIndex", questionToAdd.getSolutionIndex());
		values.put("owner", questionToAdd.getOwner());
		
		mDatabase.insert(SQLiteCacheHelper.TABLE_QUESTIONS,
				null, values);
	}
	
	/**
	 * Cleans the database (cannot be undone!)
	 */
	public void eraseDatabase() {
		sanityDatabaseCheck();
		if (mDatabase.isReadOnly()) {
			throw new IllegalStateException("Cannot wipe read-only database.");
		} else {
			//XXX does this work?
			mDatabase.delete(SQLiteCacheHelper.TABLE_QUESTIONS, "", null);
		}
	}
	
	/**
	 * Closes this object (it cannot furthermore be used to access the DB).
	 */
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

	private QuizQuestion extractQuestionFromCursor(Cursor questionCursor) {
		if (questionCursor.isBeforeFirst() || questionCursor.isAfterLast()) {
			throw new IllegalStateException("Trying to create a QuizQuestion " +
					"from Cursor pointing before or after the rows.");
		} else {
			// id, tags, statement, answer, solutionIndex, owner
			int id = questionCursor.getInt(ID_COLUMN);
			String[] tagsArray = questionCursor.getString(TAGS_COLUMN).split(", ");
			Set<String> tags = new HashSet<String>(Arrays.asList(tagsArray));
			String statement = questionCursor.getString(STATEMENT_COLUMN);
			String[] answersArray = questionCursor.getString(ANSWER_COLUMN).split(", ");
			List<String> answers = Arrays.asList(answersArray);
			int solutionIndex = questionCursor.getInt(SOLUTION_INDEX_COLUMN);
			String owner = questionCursor.getString(OWNER_COLUMN);
			
			return new QuizQuestion(statement, answers, solutionIndex, tags, id, owner);
		}
	}
}
