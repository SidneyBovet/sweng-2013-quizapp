package epfl.sweng.caching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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

	/**
	 * A content provider for the persistent cache.<br/>
	 * <i><b>NOTE:</b> It must be closed via its <code>destroy()</code>
	 * method!</i>
	 * 
	 * @param context
	 *            The activity's context
	 * @param writable
	 *            Indicates whether this content provider is read-only.
	 */
	public CacheContentProvider(Context context, boolean writable) {
		SQLiteCacheHelper openHelper = new SQLiteCacheHelper(context);

		try {
			mDatabase = writable ? openHelper.getWritableDatabase()
					: openHelper.getReadableDatabase();
		} catch (SQLiteException e) {
			Log.e("DB", "Could not open the DB.");
		}
	}

	/**
	 * 
	 * @return the number of questions currently in the outbox cache.
	 */
	public int getOutboxCount() {
		sanityDatabaseCheck();

		return (int) DatabaseUtils.queryNumEntries(mDatabase,
				SQLiteCacheHelper.TABLE_QUESTIONS,
				SQLiteCacheHelper.FIELD_QUESTIONS_IS_QUEUED + "=1");
	}

	public Cursor getQuestions(QuizQuery query) {
		sanityDatabaseCheck();

		String queryStr = query.toString();

		// We select all question ids...
		String[] selection = new String[] {SQLiteCacheHelper.FIELD_QUESTIONS_PK};
		String[] whereArgs = extractParameters(queryStr);
		String whereClause = filterQuery(queryStr);
		String orderBy = null;

		if (query.isRandom()) {
			whereClause = null;
			whereArgs = null;
			orderBy = "RANDOM()";
		}

		// TODO Query the Tags table first to get all the ids that matches the
		// query,
		// then get all the questionsId's related to it (contained in the
		// linking table).

		Cursor idCursor = mDatabase.query(SQLiteCacheHelper.TABLE_QUESTIONS,
				selection, whereClause, whereArgs, null, null, orderBy, null);

		idCursor.moveToFirst();
		return idCursor;
	}

	/**
	 * Normalizes the query: - We change the '*' by a logical AND. - We change
	 * the '+' by a logical OR. - We change the ' ' by a logical AND.
	 * 
	 * @param query
	 *            Query to be changed.
	 * @return the new SQL-compatible query.
	 */
	private String filterQuery(String query) {

		query.replaceAll("(?:\\ )*\\*(?:\\ )*", " AND ");
		query.replaceAll("\\ \\+\\ ", " OR ");
		query.replaceAll("\\ ", "AND");

		// TODO CHANGE THAT! A QUERY SHOULD BE MADE TO THE
		// TAGS TABLE (or the linking table).
		query.replaceAll("\\w", "tags = ?");

		return query;
	}

	/**
	 * Will get all the words contained in the query given in parameters.
	 * 
	 * @param query
	 *            Query from where we need to extract the data.
	 * @return All the words contained in the query.
	 */
	private String[] extractParameters(String query) {

		List<String> whereArgsArray = new ArrayList<String>();

		// Finds all alphanumeric tokens in the query
		Pattern pattern = Pattern.compile("\\w");
		Matcher m = pattern.matcher(query);
		while (m.find()) {
			whereArgsArray.add(m.group());
		}

		// We convert the List to an array of String.
		return (String[]) whereArgsArray.toArray(new String[whereArgsArray
				.size()]);
	}

	public QuizQuestion getQuestionFromPK(int id) {

		// Step 2 : Get all the answers and tags for that question
		List<String> answers = retrieveAnswers(id);
		Set<String> tags = retrieveTags(id);

		// Step 3 : Store question content into variables.
		Cursor questionCursor = mDatabase.query(
				SQLiteCacheHelper.TABLE_QUESTIONS, new String[] {
					SQLiteCacheHelper.FIELD_QUESTIONS_SWENG_ID,
					SQLiteCacheHelper.FIELD_QUESTIONS_STATEMENT,
					SQLiteCacheHelper.FIELD_QUESTIONS_SOLUTION_INDEX,
					SQLiteCacheHelper.FIELD_QUESTIONS_OWNER },
				SQLiteCacheHelper.FIELD_QUESTIONS_PK + " = ?",
				new String[] {String.valueOf(id)}, null, null, null, null);

		questionCursor.moveToFirst();

		int questionId = questionCursor.getInt(questionCursor
				.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_SWENG_ID));
		String statement = questionCursor.getString(questionCursor
				.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_STATEMENT));
		int solutionIndex = questionCursor
				.getInt(questionCursor
						.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_SOLUTION_INDEX));
		String owner = questionCursor.getString(questionCursor
				.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_OWNER));

		questionCursor.close();

		// Step 4 : Create the new question and return it
		return new QuizQuestion(statement, answers, solutionIndex, tags,
				questionId, owner);
	}

	/**
	 * Adds a {@link QuizQuestion}to the persistent cache.
	 * 
	 * @param question
	 *            The question to be added to the cache
	 */
	public void addQuizQuestion(QuizQuestion question) {

		sanityDatabaseCheck();
		long id = insertSimplifiedQuestion(question.getId(),
				question.getOwner(), question.getStatement(),
				question.getSolutionIndex());
		insertQuestionAnswers(id, question.getAnswers());
		insertQuestionTags(id, question.getTags());
	}

	/**
	 * Cleans the database (cannot be undone!)
	 */
	public void eraseDatabase() {
		sanityDatabaseCheck();
		if (mDatabase.isReadOnly()) {
			throw new IllegalStateException("Cannot wipe read-only database.");
		} else {
			mDatabase.execSQL("DROP TABLE IF EXISTS " +
					SQLiteCacheHelper.TABLE_ANSWERS);
			mDatabase.execSQL("DROP TABLE IF EXISTS " +
					SQLiteCacheHelper.TABLE_QUESTIONS_TAGS);
			mDatabase.execSQL("DROP TABLE IF EXISTS " +
					SQLiteCacheHelper.TABLE_TAGS);
			mDatabase.execSQL("DROP TABLE IF EXISTS " +
					SQLiteCacheHelper.TABLE_QUESTIONS);
		}
	}

	/**
	 * Closes this object (it cannot furthermore be used to access the DB).
	 */
	public void close() {
		sanityDatabaseCheck();
		mDatabase.close();
	}

	public boolean isClosed() {
		return !mDatabase.isOpen();
	}

	/*********************** Private methods ***********************/

	private void sanityDatabaseCheck() {
		// note that use of isDatabaseIntegrityOk() may take a long time,
		// it is therefore avoided here.
		if (null == mDatabase || !mDatabase.isOpen()) {
			throw new IllegalStateException(
					"The database object is either null or closed");
		}
	}

	private void insertQuestionTags(long id, Set<String> tags) {
		for (String tag : tags) {

			ContentValues tagValues = new ContentValues(1);
			tagValues.put(SQLiteCacheHelper.FIELD_TAGS_NAME, tag);
			long tagId = mDatabase.insert(SQLiteCacheHelper.TABLE_TAGS, null,
					tagValues);

			// Updates linking table between tags and questions tables.
			ContentValues tagQuestionValues = new ContentValues(2);
			tagQuestionValues.put(
					SQLiteCacheHelper.FIELD_QUESTIONS_TAGS_TAG_FK, tagId);
			tagQuestionValues.put(
					SQLiteCacheHelper.FIELD_QUESTIONS_TAGS_QUESTION_FK, id);
			mDatabase.insert(SQLiteCacheHelper.TABLE_QUESTIONS_TAGS, null,
					tagQuestionValues);
		}
	}

	private void insertQuestionAnswers(long id, List<String> answers) {
		for (String answer : answers) {
			ContentValues values = new ContentValues(2);
			values.put(SQLiteCacheHelper.FIELD_ANSWERS_ANSWER_VALUE, answer);
			values.put(SQLiteCacheHelper.FIELD_ANSWERS_QUESTION_FK, id);
			mDatabase.insert(SQLiteCacheHelper.TABLE_ANSWERS, null, values);
		}
	}

	/**
	 * Note that both the questionId and the owner can be set to null.
	 * 
	 * @param questionId
	 * @param owner
	 * @param statement
	 * @param solutionIndex
	 * @return
	 */
	private long insertSimplifiedQuestion(long questionId, String owner,
			String statement, int solutionIndex) {
		ContentValues values = new ContentValues(4);
		values.put(SQLiteCacheHelper.FIELD_QUESTIONS_SWENG_ID, questionId);
		values.put(SQLiteCacheHelper.FIELD_QUESTIONS_OWNER, owner);
		values.put(SQLiteCacheHelper.FIELD_QUESTIONS_STATEMENT, statement);
		values.put(SQLiteCacheHelper.FIELD_QUESTIONS_SOLUTION_INDEX,
				solutionIndex);

		long id = mDatabase.insert(SQLiteCacheHelper.TABLE_QUESTIONS, null,
				values);

		return id;
	}

	private Set<String> retrieveTags(int id) {

		// Get ids of all the current question tags.
		Cursor tagsIdCursor = mDatabase.query(
				SQLiteCacheHelper.TABLE_QUESTIONS_TAGS,
				new String[] {SQLiteCacheHelper.FIELD_QUESTIONS_TAGS_TAG_FK},
				SQLiteCacheHelper.FIELD_QUESTIONS_TAGS_QUESTION_FK + " = ?",
				new String[] {String.valueOf(id)}, null, null,
				SQLiteCacheHelper.FIELD_QUESTIONS_TAGS_QUESTION_FK + " ASC",
				null);

		ArrayList<String> tagsId = new ArrayList<String>();
		if (tagsIdCursor.moveToFirst()) {
			do {
				tagsId.add(String.valueOf(tagsIdCursor.getInt(tagsIdCursor
						.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_TAGS_TAG_FK))));
			} while (tagsIdCursor.moveToNext());
		}

		// Get all the tags related to questionId given in parameter.
		String query = "SELECT " + SQLiteCacheHelper.FIELD_TAGS_NAME + " FROM "
				+ SQLiteCacheHelper.TABLE_TAGS + " WHERE id IN ("
				+ makePlaceholders(tagsId.size()) + ");";
		Cursor tagsCursor = mDatabase.rawQuery(query,
				(String[]) tagsId.toArray(new String[tagsId.size()]));

		HashSet<String> tags = new HashSet<String>();
		if (tagsCursor.moveToFirst()) {
			do {
				tags.add(tagsCursor.getString(tagsCursor
						.getColumnIndex(SQLiteCacheHelper.FIELD_TAGS_NAME)));
			} while (tagsCursor.moveToNext());
		}

		tagsCursor.close();

		return tags;
	}

	// I got that function from
	// http://stackoverflow.com/questions/7418849/android-sqlite-in-clause-and-placeholders
	// even though it's not a complex piece of code.
	private String makePlaceholders(int len) {
		if (len < 1) {
			// It will lead to an invalid query anyway ..
			throw new RuntimeException("No placeholders");
		} else {
			StringBuilder sb = new StringBuilder(len * 2 - 1);
			sb.append("?");
			for (int i = 1; i < len; i++) {
				sb.append(",?");
			}
			return sb.toString();
		}
	}

	private List<String> retrieveAnswers(int id) {

		Cursor answersCursor = mDatabase.query(SQLiteCacheHelper.TABLE_ANSWERS,
				new String[] {SQLiteCacheHelper.FIELD_ANSWERS_ANSWER_VALUE},
				SQLiteCacheHelper.FIELD_ANSWERS_QUESTION_FK + " = ?",
				new String[] {String.valueOf(id)}, null, null,
				SQLiteCacheHelper.FIELD_ANSWERS_PK + " ASC", null);

		ArrayList<String> answers = new ArrayList<String>();
		if (answersCursor.moveToFirst()) {
			do {
				answers.add(answersCursor.getString(answersCursor
						.getColumnIndex(SQLiteCacheHelper.FIELD_ANSWERS_ANSWER_VALUE)));
			} while (answersCursor.moveToNext());
		}

		answersCursor.close();

		return answers;
	}
}
