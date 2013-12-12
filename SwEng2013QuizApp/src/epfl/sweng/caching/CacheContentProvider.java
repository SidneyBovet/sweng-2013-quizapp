package epfl.sweng.caching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import epfl.sweng.app.SwEng2013QuizApp;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * Object providing interface to local questions database.
 * 
 * @author Sidney
 * 
 */
public class CacheContentProvider {

	private static final int QUESTIONS_NB_FIELDS_FETCHED = 4;
	private SQLiteDatabase mDatabase = null;

	/**
	 * A content provider for the persistent cache.<br/>
	 * <i><b>NOTE:</b> It must be closed via its <code>destroy()</code>
	 * method!</i>
	 * 
	 * @param writable
	 *            Indicates whether this content provider is read-only.
	 */
	public CacheContentProvider(boolean writable) {
		SQLiteCacheHelper openHelper = new SQLiteCacheHelper(
				SwEng2013QuizApp.getContext());
		try {
			mDatabase = writable ? openHelper.getWritableDatabase()
					: openHelper.getReadableDatabase();
		} catch (SQLiteException e) {
			Log.e("DB", "Could not open the DB.", e);
		}
	}

	/**
	 * Returns the set of IDs that match the tag given in parameter.
	 * 
	 * @param tag
	 *            The wanted tag in the question.
	 * @return The set of IDs that match the tag given in parameter.
	 */
	public Set<Long> getQuestionsIdsWithTag(String tag) {

		Set<Long> questionsIds = new HashSet<Long>();

		String query = "SELECT "
				+ SQLiteCacheHelper.FIELD_QUESTIONS_TAGS_QUESTION_FK + " FROM "
				+ SQLiteCacheHelper.TABLE_QUESTIONS_TAGS + " INNER JOIN "
				+ SQLiteCacheHelper.TABLE_TAGS + " ON "
				+ SQLiteCacheHelper.TABLE_QUESTIONS_TAGS + "."
				+ SQLiteCacheHelper.FIELD_QUESTIONS_TAGS_TAG_FK + "="
				+ SQLiteCacheHelper.TABLE_TAGS + "."
				+ SQLiteCacheHelper.FIELD_TAGS_PK + " WHERE "
				+ SQLiteCacheHelper.TABLE_TAGS + "."
				+ SQLiteCacheHelper.FIELD_TAGS_NAME + "=?";

		Cursor questionsIdsCursor = mDatabase.rawQuery(query,
				new String[] {tag});

		if (questionsIdsCursor.moveToFirst()) {
			do {
				questionsIds
						.add(questionsIdsCursor.getLong(questionsIdsCursor
								.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_TAGS_QUESTION_FK)));
			} while (questionsIdsCursor.moveToNext());
		}

		return questionsIds;
	}

	/**
	 * Returns the pointer to the questions matching the query given in
	 * parameter.
	 * 
	 * @param query
	 *            The query used in order to get the wanted questions.
	 * @return The pointer to the questions matching the query.
	 */
	public Cursor getQuestions(QuizQuery query) {
		sanityDatabaseCheck();

		// e.g. "A * B    + C (D + E * F)" or st else.
		String queryStr = query.toString();

		// We select all question ids...
		String[] selection = new String[] {SQLiteCacheHelper.FIELD_QUESTIONS_PK};
		String whereClause = null;
		String[] whereArgs = null;
		String orderBy = null;

		if (query.isRandom()) {
			orderBy = "RANDOM()";
			Cursor randomQuestionIdCursor = mDatabase.query(
					SQLiteCacheHelper.TABLE_QUESTIONS, selection, whereClause,
					whereArgs, null, null, orderBy, null);
			randomQuestionIdCursor.moveToFirst();
			return randomQuestionIdCursor;
		} else {

			String[] tagsArray = SQLHelper.extractParameters(queryStr);

			List<Set<Long>> questionsIdsList = new ArrayList<Set<Long>>();
			for (String tag : tagsArray) {
				questionsIdsList.add(getQuestionsIdsWithTag(tag));
			}

			queryStr = SQLHelper.filterQuery(queryStr);
			String[] tokens = queryStr.split("(?!^)");
			List<String> tokensAsList = new ArrayList<String>(
					Arrays.asList(tokens));

			Set<Long> idsMatchingQuery = TagsAlgorithmHelper.evaluate(
					tokensAsList, questionsIdsList);
			String correspondingSQLiteArray = SQLHelper
					.setToSQLiteQueryArray(idsMatchingQuery);

			String rawQuery = "SELECT " + SQLiteCacheHelper.FIELD_QUESTIONS_PK
					+ " FROM " + SQLiteCacheHelper.TABLE_QUESTIONS + " WHERE "
					+ SQLiteCacheHelper.FIELD_QUESTIONS_PK + " IN "
					+ correspondingSQLiteArray + ";";

			Cursor questionsCursor = mDatabase.rawQuery(rawQuery, null);
			questionsCursor.moveToFirst();
			return questionsCursor;
		}
	}

	/**
	 * Retrieve the {@link QuizQuestion} with the id matching the id given in
	 * parameter from the cache.
	 * 
	 * @param id
	 *            The identifier of the question.
	 * @return The wanted {@link QuizQuestion}.
	 */
	public QuizQuestion getQuestionFromPK(long id) {

		// Step 1 : Get all the answers and tags for that question
		List<String> answers = retrieveAnswers(id);
		Set<String> tags = retrieveTags(id);

		// Step 2 : Store question content into variables.
		Cursor questionCursor = mDatabase.query(
				SQLiteCacheHelper.TABLE_QUESTIONS, new String[] {
					SQLiteCacheHelper.FIELD_QUESTIONS_SWENG_ID,
					SQLiteCacheHelper.FIELD_QUESTIONS_STATEMENT,
					SQLiteCacheHelper.FIELD_QUESTIONS_SOLUTION_INDEX,
					SQLiteCacheHelper.FIELD_QUESTIONS_OWNER },
				SQLiteCacheHelper.FIELD_QUESTIONS_PK + " = ?",
				new String[] {String.valueOf(id)}, null, null, null, null);

		if (questionCursor.moveToFirst()) {
			int questionId = questionCursor
					.getInt(questionCursor
							.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_SWENG_ID));
			String statement = questionCursor
					.getString(questionCursor
							.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_STATEMENT));
			int solutionIndex = questionCursor
					.getInt(questionCursor
							.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_SOLUTION_INDEX));
			String owner = questionCursor.getString(questionCursor
					.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_OWNER));

			questionCursor.close();

			// Step 3 : Create the new question and return it
			return new QuizQuestion(statement, answers, solutionIndex, tags,
					questionId, owner);
		}

		return null;
	}

	/**
	 * Adds a question to the cache.
	 * 
	 * @param question
	 *            Question to be stored in cache.
	 * @return the PK of the newly added question.
	 */
	public long addQuizQuestion(QuizQuestion question) {
		sanityDatabaseCheck();
		long id = insertSimplifiedQuestion(question.getId(),
				question.getOwner(), question.getStatement(),
				question.getSolutionIndex());
		insertQuestionAnswers(id, question.getAnswers());
		insertQuestionTags(id, question.getTags());

		return id;
	}

	/**
	 * Cleans the database (cannot be undone!)
	 */
	public void eraseDatabase() {
		sanityDatabaseCheck();
		if (mDatabase.isReadOnly()) {
			throw new IllegalStateException("Cannot wipe read-only database.");
		} else {
			mDatabase.execSQL("DROP TABLE IF EXISTS "
					+ SQLiteCacheHelper.TABLE_ANSWERS);
			mDatabase.execSQL("DROP TABLE IF EXISTS "
					+ SQLiteCacheHelper.TABLE_QUESTIONS_TAGS);
			mDatabase.execSQL("DROP TABLE IF EXISTS "
					+ SQLiteCacheHelper.TABLE_TAGS);
			mDatabase.execSQL("DROP TABLE IF EXISTS "
					+ SQLiteCacheHelper.TABLE_QUESTIONS);

			mDatabase.execSQL(SQLiteCacheHelper.CREATE_TABLE_QUESTIONS);
			mDatabase.execSQL(SQLiteCacheHelper.CREATE_TABLE_TAGS);
			mDatabase.execSQL(SQLiteCacheHelper.CREATE_TABLE_QUESTIONS_TAGS);
			mDatabase.execSQL(SQLiteCacheHelper.CREATE_TABLE_ANSWERS);
		}
	}

	/**
	 * Closes this object (it cannot furthermore be used to access the DB).
	 */
	public void close() {
		sanityDatabaseCheck();
		mDatabase.close();
	}

	/**
	 * Indicate if the cache is accessible.
	 * 
	 * @return true if accessible, false otherwise.
	 */
	public boolean isClosed() {
		return null == mDatabase || !mDatabase.isOpen();
	}

	/**
	 * Give the size of the inbox
	 * 
	 * @return the size of the inbox
	 */
	public int getInboxSize() {
		sanityDatabaseCheck();
		return (int) DatabaseUtils.queryNumEntries(mDatabase,
				SQLiteCacheHelper.TABLE_QUESTIONS,
				null);
	}
	/*********************** Private methods ***********************/

	/**
	 * 
	 * @param id
	 *            id of the question in the cache.
	 * @return A list of answers if they exist, null otherwise.
	 */
	private List<String> retrieveAnswers(long id) {

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

	/**
	 * Get ids of all the current question tags.
	 * 
	 * @param id
	 *            id of the question in the cache.
	 * @return A list of tags if they exist, null otherwise.
	 */
	private Set<String> retrieveTags(long id) {

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
		} else {
			tagsIdCursor.close();
			return null;
		}

		tagsIdCursor.close();

		// Get all the tags related to questionId given in parameter.
		String query = "SELECT " + SQLiteCacheHelper.FIELD_TAGS_NAME + " FROM "
				+ SQLiteCacheHelper.TABLE_TAGS + " WHERE "
				+ SQLiteCacheHelper.FIELD_TAGS_PK + " IN ("
				+ SQLHelper.makePlaceholders(tagsId.size()) + ");";
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

	/**
	 * Adds the tags given in parameters to the question with id given in
	 * parameter.
	 * 
	 * @param id
	 *            the id of the question to modify.
	 * @param tags
	 *            the tags to add to the question.
	 */
	private void insertQuestionTags(long id, Set<String> tags) {
		for (String tag : tags) {

			ContentValues tagValues = new ContentValues(1);
			tagValues.put(SQLiteCacheHelper.FIELD_TAGS_NAME, tag);
			long tagId = mDatabase.insert(SQLiteCacheHelper.TABLE_TAGS, null,
					tagValues);

			if (tagId == -1) {
				throw new SQLiteException("Cannot insert question in table "
						+ SQLiteCacheHelper.TABLE_TAGS);
			}

			// Updates linking table between tags and questions tables.
			ContentValues tagQuestionValues = new ContentValues(2);
			tagQuestionValues.put(
					SQLiteCacheHelper.FIELD_QUESTIONS_TAGS_TAG_FK, tagId);
			tagQuestionValues.put(
					SQLiteCacheHelper.FIELD_QUESTIONS_TAGS_QUESTION_FK, id);
			if (mDatabase.insert(SQLiteCacheHelper.TABLE_QUESTIONS_TAGS, null,
					tagQuestionValues) == -1) {
				throw new SQLiteException("Cannot insert question in table "
						+ SQLiteCacheHelper.TABLE_QUESTIONS_TAGS);
			}

		}
	}

	/**
	 * Adds the answers given in parameters to the question with id given in
	 * parameter.
	 * 
	 * @param id
	 *            the id of the question to modify.
	 * @param answers
	 *            the answers to add to the question.
	 */
	private void insertQuestionAnswers(long id, List<String> answers) {
		for (String answer : answers) {
			ContentValues values = new ContentValues(2);
			values.put(SQLiteCacheHelper.FIELD_ANSWERS_ANSWER_VALUE, answer);
			values.put(SQLiteCacheHelper.FIELD_ANSWERS_QUESTION_FK, id);
			if (mDatabase.insert(SQLiteCacheHelper.TABLE_ANSWERS, null, values) == -1) {
				throw new SQLiteException("Cannot insert question in table "
						+ SQLiteCacheHelper.TABLE_ANSWERS);
			}
		}
	}

	/**
	 * Insert a "simplified" question in the cache.
	 * 
	 * @param questionId
	 *            question id.
	 * @param owner
	 *            question owner.
	 * @param statement
	 *            statement of the question.
	 * @param solutionIndex
	 *            index of the answer.
	 * @return the id of the question created.
	 */
	private long insertSimplifiedQuestion(long questionId, String owner,
			String statement, int solutionIndex) {
		ContentValues values = new ContentValues(QUESTIONS_NB_FIELDS_FETCHED);
		values.put(SQLiteCacheHelper.FIELD_QUESTIONS_SWENG_ID, questionId);
		values.put(SQLiteCacheHelper.FIELD_QUESTIONS_OWNER, owner);
		values.put(SQLiteCacheHelper.FIELD_QUESTIONS_STATEMENT, statement);
		values.put(SQLiteCacheHelper.FIELD_QUESTIONS_SOLUTION_INDEX,
				solutionIndex);

		long id = mDatabase.insert(SQLiteCacheHelper.TABLE_QUESTIONS, null,
				values);

		if (id == -1) {
			throw new SQLiteException("Cannot insert question in table "
					+ SQLiteCacheHelper.TABLE_QUESTIONS);
		}

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