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
	
	public Cursor getQuestions(QuizQuery query) {
		sanityDatabaseCheck();
		
		String[] selection = new String[] {"id"};
		String whereClause = query.toString();
		ArrayList<String> whereArgsArray = new ArrayList<String>();
		
		// Finds all alphanumeric tokens...
		Pattern pattern = Pattern.compile("\\w");
		Matcher m = pattern.matcher(whereClause);
		while (m.find()) {
		    whereArgsArray.add(m.group());
		}

		String[] whereArgs = (String[]) whereArgsArray.toArray();
		
		// ...normalizes the expression...
		whereClause.replaceAll("(?:\\ )*\\*(?:\\ )*", " AND ");
		whereClause.replaceAll("\\ \\+\\ ", " OR ");
		whereClause.replaceAll("\\ ", "AND");
		
		// ... and replaces them with '?'
		whereClause.replaceAll("\\w", "tags = ?");

		String orderBy = null;

		if (query.isRandom()) {
			whereClause = null;
			whereArgs = null;
			orderBy = "RANDOM()";
		}

		Cursor idCursor = mDatabase.query(SQLiteCacheHelper.TABLE_QUESTIONS,
				selection, whereClause, whereArgs, null, null, orderBy, null);

		idCursor.moveToFirst();
		return idCursor;
	}

	public QuizQuestion getQuestionFromPK(int id) {

		// Step 2 : Get all the answers and tags for that question
		List<String> answers = retrieveAnswers(id);
		Set<String> tags = retrieveTags(id);

		// Step 3 : Store question content into variables.
		Cursor questionCursor = mDatabase.query(
				SQLiteCacheHelper.TABLE_QUESTIONS, new String[] {"questionId",
					"statement, solutionIndex, owner"}, "id = ?",
				new String[] {String.valueOf(id)}, null, null, null, null);

		questionCursor.moveToFirst();
		int questionId = questionCursor.getInt(questionCursor
				.getColumnIndex("questionId"));
		String statement = questionCursor.getString(questionCursor
				.getColumnIndex("statement"));
		int solutionIndex = questionCursor.getInt(questionCursor
				.getColumnIndex("solutionIndex"));
		String owner = questionCursor.getString(questionCursor
				.getColumnIndex("owner"));

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
		long questionId = insertSimplifiedQuestion(question.getId(),
				question.getOwner(), question.getStatement(),
				question.getSolutionIndex());
		insertQuestionAnswers(questionId, question.getAnswers());
		insertQuestionTags(questionId, question.getTags());
	}

	/**
	 * Cleans the database (cannot be undone!)
	 * 
	 * XXX I'm seriously wondering if this method should be public or existing
	 * at all...
	 */
	public void eraseDatabase() {
		sanityDatabaseCheck();
		if (mDatabase.isReadOnly()) {
			throw new IllegalStateException("Cannot wipe read-only database.");
		} else {
			// XXX does this work?
			mDatabase.delete(SQLiteCacheHelper.TABLE_QUESTIONS, "", null);
		}
	}

	/**
	 * Closes this object (it cannot furthermore be used to access the DB).
	 */
	public void close() {
		sanityDatabaseCheck();
		mDatabase.close();
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

	private void insertQuestionTags(long questionId, Set<String> tags) {
		for (String tag : tags) {
			/*
			 * XXX Note that I did not want to expose fields of the DB in the
			 * SQLiteCacheHelper so I hardcoded them here. It might be a good
			 * idea to change that later.
			 */
			// Updates tags table.
			ContentValues tagValues = new ContentValues(1);
			tagValues.put("name", tag);
			long tagId = mDatabase.insert(SQLiteCacheHelper.TABLE_TAGS, null,
					tagValues);

			// Updates linking table between tags and questions tables.
			ContentValues tagQuestionValues = new ContentValues(2);
			tagQuestionValues.put("tagId", tagId);
			tagQuestionValues.put("questionId", questionId);
			mDatabase.insert(SQLiteCacheHelper.TABLE_QUESTIONS_TAGS, null,
					tagValues);
		}
	}

	private void insertQuestionAnswers(long questionId, List<String> answers) {
		for (String answer : answers) {
			ContentValues values = new ContentValues(2);
			/*
			 * XXX Note that I did not want to expose fields of the DB in the
			 * SQLiteCacheHelper so I hardcoded them here. It might be a good
			 * idea to change that later.
			 */
			values.put("content", answer);
			values.put("questionId", questionId);
			mDatabase.insert(SQLiteCacheHelper.TABLE_ANSWERS, null, values);
		}
	}

	/**
	 * Note that both the questionId and the owner can be set to null.
	 * 
	 * @param questionId
	 * @param owner
	 * @param statement
	 * @param solutionId
	 * @return
	 */
	private long insertSimplifiedQuestion(long questionId, String owner,
			String statement, int solutionId) {

		/*
		 * XXX Note that I did not want to expose fields of the DB in the
		 * SQLiteCacheHelper so I hardcoded them here. It might be a good idea
		 * to change that later. (Note the checkstyle issue aswell).
		 */
		ContentValues values = new ContentValues(4);
		values.put("questionId", questionId);
		values.put("owner", questionId);
		values.put("statement", questionId);
		values.put("solutionId", questionId);

		mDatabase.insert(SQLiteCacheHelper.TABLE_QUESTIONS, null, values);

		return questionId;
	}

	private Set<String> retrieveTags(int id) {

		// Get ids of all the current question tags.
		Cursor tagsIdCursor = mDatabase.query(
				SQLiteCacheHelper.TABLE_QUESTIONS_TAGS,
				new String[] {"tagId"}, "questionId = ?",
				new String[] {String.valueOf(id)}, null, null,
				"id ASC", null);

		ArrayList<Integer> tagsId = new ArrayList<Integer>();
		if (tagsIdCursor.moveToFirst()) {
			do {
				tagsId.add(tagsIdCursor.getInt(tagsIdCursor
						.getColumnIndex("tagId")));
			} while (tagsIdCursor.moveToNext());
		}

		// Get all the tags related to questionId given in parameter.
		String query = "SELECT name FROM " + SQLiteCacheHelper.TABLE_TAGS
				+ " WHERE id IN (" + makePlaceholders(tagsId.size()) + ");";
		Cursor tagsCursor = mDatabase.rawQuery(query,
				(String[]) tagsId.toArray());

		HashSet<String> tags = new HashSet<String>();
		if (tagsCursor.moveToFirst()) {
			do {
				tags.add(tagsCursor.getString(tagsCursor.getColumnIndex("name")));
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
				new String[] {"content"}, "questionId = ?",
				new String[] {String.valueOf(id)}, null, null,
				"id ASC", null);

		ArrayList<String> answers = new ArrayList<String>();
		if (answersCursor.moveToFirst()) {
			do {
				answers.add(answersCursor.getString(answersCursor
						.getColumnIndex("content")));
			} while (answersCursor.moveToNext());
		}

		answersCursor.close();

		return answers;
	}
}
