package epfl.sweng.caching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		SQLiteCacheHelper openHelper =
				new SQLiteCacheHelper(SwEng2013QuizApp.getContext());

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
			/* fake cursor to avoid NPE *
			orderBy = "RANDOM()";
			Cursor randomQuestionIdCursor = mDatabase.query(
					SQLiteCacheHelper.TABLE_QUESTIONS, selection, whereClause,
						whereArgs, null, null, orderBy, null);
			randomQuestionIdCursor.moveToFirst();
			return randomQuestionIdCursor;
			/* end of fake cursor to avoid NPE */
			
			
			String[] tagsArray = extractParameters(queryStr);

			List<Set<Long>> questionsIdsList = new ArrayList<Set<Long>>();
			for (String tag : tagsArray) {
				questionsIdsList.add(getQuestionsIdsWithTag(tag));
			}

			queryStr = filterQuery(queryStr);
			String[] tokens = queryStr.split("(?!^)");
			List<String> tokensAsList = new ArrayList<String>(Arrays.asList(tokens));
			
			Set<Long> idsMatchingQuery = evaluate(tokensAsList, questionsIdsList);
			String correspondingSQLiteArray = setToSQLiteQueryArray(idsMatchingQuery);
			
			String rawQuery = "SELECT * FROM " + SQLiteCacheHelper.TABLE_QUESTIONS +
					" WHERE " + SQLiteCacheHelper.FIELD_QUESTIONS_PK + " IN " +
					correspondingSQLiteArray + ";";
			
			Cursor questionsCursor = mDatabase.rawQuery(rawQuery, null);
			questionsCursor.moveToFirst();
			return questionsCursor;
			
			// What to use for the next step?
			// - String[] tokensAsList
			// - List<Set<Long>> questionsIdsList

			// What to do?
			// 1 - Find the deeply-nested block, then evaluate it with
			// evaluateBlock
			// with the following rules:
			// a) Multiplication first, addition afterwards
			// b) Do it until the block contains one element
			// 2 - Repeat step 1 until questionsIdsList.size() == 1
			
			// Last but not least, when finished please do the following:
			// 		1 - Return something
			// 		2 - Remove the block above: /* fake cursor to avoid NPE */
/************************ TO UNCOMMENT *********************************/
		}
	}

	public QuizQuestion getQuestionFromPK(long id) {

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

			// Step 4 : Create the new question and return it
			return new QuizQuestion(statement, answers, solutionIndex, tags,
					questionId, owner);
		}

		return null;
	}

	/**
	 * 
	 * @param question
	 *            Question to be stored in cache (not in outbox).
	 * @return the PK of the newly added question.
	 */
	public long addQuizQuestion(QuizQuestion question) {
		return addQuizQuestion(question, false);
	}

	/**
	 * Adds a {@link QuizQuestion}to the persistent cache.
	 * 
	 * @param question
	 *            The question to be added to the cache
	 * @return the PK of the newly added question.
	 */
	public long addQuizQuestion(QuizQuestion question, boolean wantInOutbox) {
		sanityDatabaseCheck();
		long id = insertSimplifiedQuestion(question.getId(),
				question.getOwner(), question.getStatement(),
				question.getSolutionIndex());
		insertQuestionAnswers(id, question.getAnswers());
		insertQuestionTags(id, question.getTags());

		if (wantInOutbox) {
			putQuestionInOutbox(id);
		}

		return id;
	}

	/**
	 * Returns a copy of the first question in the outbox.
	 * 
	 * @return the first question in the outbox.
	 */
	public QuizQuestion peekFirstQuestionFromOutbox() {
		return getQuestionFromPK(getIdOfFirstQuestionInOutbox());
	}

	/**
	 * Removes and returns the first question in the outbox.
	 * 
	 * @return First question in the outbox.
	 */
	public QuizQuestion takeFirstQuestionFromOutbox() {

		int id = getIdOfFirstQuestionInOutbox();

		if (-1 != id) {
			takeQuestionOutOfOutbox(id);
			return getQuestionFromPK(id);
		}

		return null;
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

	public boolean isClosed() {
		return !mDatabase.isOpen();
	}

	/*********************** Private methods ***********************/

	private int getIdOfFirstQuestionInOutbox() {

		int id = -1;

		// Get the first question in the outbox stack.
		Cursor questionOutboxIdCursor = mDatabase.query(
				SQLiteCacheHelper.TABLE_QUESTIONS,
				new String[] {SQLiteCacheHelper.FIELD_QUESTIONS_PK},
				SQLiteCacheHelper.FIELD_QUESTIONS_IS_QUEUED + "=1", null, null,
				null, SQLiteCacheHelper.FIELD_QUESTIONS_PK + " ASC", "1");

		if (questionOutboxIdCursor.moveToFirst()) {
			id = questionOutboxIdCursor.getInt(questionOutboxIdCursor
					.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_PK));

			questionOutboxIdCursor.close();
		}

		return id;
	}

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
		} else {
			// No answers found.
		}

		answersCursor.close();

		return answers;
	}

	/**
	 * 
	 * @param id
	 *            id of the question in the cache.
	 * @return A list of tags if they exist, null otherwise.
	 */
	private Set<String> retrieveTags(long id) {

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
		} else {
			tagsIdCursor.close();
			return null;
		}

		tagsIdCursor.close();

		// Get all the tags related to questionId given in parameter.
		String query = "SELECT " + SQLiteCacheHelper.FIELD_TAGS_NAME + " FROM "
				+ SQLiteCacheHelper.TABLE_TAGS + " WHERE "
				+ SQLiteCacheHelper.FIELD_TAGS_PK + " IN ("
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

	private void putQuestionInOutbox(long id) {
		changeQuestionOutboxStatus(id, true);
	}

	private void takeQuestionOutOfOutbox(long id) {
		changeQuestionOutboxStatus(id, false);
	}

	private void changeQuestionOutboxStatus(long id, boolean questionInOutbox) {
		ContentValues isQueuedValue = new ContentValues(1);
		isQueuedValue.put(SQLiteCacheHelper.FIELD_QUESTIONS_IS_QUEUED,
				questionInOutbox ? 1 : 0);
		mDatabase.update(SQLiteCacheHelper.TABLE_QUESTIONS, isQueuedValue,
				SQLiteCacheHelper.FIELD_QUESTIONS_PK + "=?",
				new String[] {String.valueOf(id)});
	}

	private void sanityDatabaseCheck() {
		// note that use of isDatabaseIntegrityOk() may take a long time,
		// it is therefore avoided here.
		if (null == mDatabase || !mDatabase.isOpen()) {
			throw new IllegalStateException(
					"The database object is either null or closed");
		}
	}

	/**
	 * I got that function from
	 * http://stackoverflow.com/questions/7418849/android
	 * -sqlite-in-clause-and-placeholders even though it's not a complex piece
	 * of code.
	 * 
	 * @param len
	 * @return
	 */
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

	/**
	 * Normalizes the query: - We change the '*' by a logical AND. - We change
	 * the '+' by a logical OR. - We change the ' ' by a logical AND.
	 * 
	 * @param query
	 *            Query to be changed.
	 * @return the new SQL-compatible query.
	 */
	private String filterQuery(String query) {

		// We start with "  (  A * B C + D (  E + F ))  "

		// Only one space max. between words.
		// " ( A * B C + D ( E + F )) "
		query = query.replaceAll("(\\ )+", " ");

		// Removes the spaces after '(' and/or before ')'
		// " (A * B C + D (E + F)) "
		query = query.replaceAll("\\(\\ ", "(");
		query = query.replaceAll("\\ \\)", ")");

		// Removes beginning and end spaces.
		// "(A * B C + D (E + F))"
		query = query.replaceAll("^\\ ", "");
		query = query.replaceAll("\\ $", "");

		// Replaces all the names by ? for the SQL query.
		// "(? * ? ? + ? (? + ?))"
		query = query.replaceAll("\\w+", "?");

		// Makes the " * " or " + " look like "*" or "+"
		// "(?*? ?+? (?+?))"
		query = query.replaceAll("(?:\\ )?\\*(?:\\ )?", "*");
		query = query.replaceAll("(?:\\ )?\\+(?:\\ )?", "+");

		// Replaces all the spaces by ANDs (the order
		// is important, do not move it without a valid reason).
		// "(?*?*?+?*(?+?))"
		query = query.replaceAll("\\ ", "*");

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
		Pattern pattern = Pattern.compile("\\w+");
		Matcher m = pattern.matcher(query);
		while (m.find()) {
			whereArgsArray.add(m.group());
		}

		// We convert the List to an array of String.
		return (String[]) whereArgsArray.toArray(new String[whereArgsArray
				.size()]);
	}
	
	private String setToSQLiteQueryArray(Set<Long> set) {
		String statement = "";
		for (Object object : set) {
			statement = statement + "," + object.toString();
		}
		if (statement.isEmpty()) {
			return "()";
		}
		statement = statement + ")";
		statement = "(" + statement.substring(1);
		return statement;
	}
	
	//pre-condition: # of '?' in normalizedTagList == questionSetList.size()
	public Set<Long> evaluate(List<String> normalizedTagList, List<Set<Long>> questionsSetList) {
		while (normalizedTagList.contains(")")) {
			List<String> originalNormalizedTagList = new ArrayList<String>(normalizedTagList);
			int firstClosingParenthesisIndex = normalizedTagList.indexOf(")");
			int correspondingOpeningParenthesisIndex = 0;
			
			List<String> expressionParenthesized = new ArrayList<String>();
			normalizedTagList.remove(firstClosingParenthesisIndex);
			for (int i = firstClosingParenthesisIndex-1; i >= 0; i--) {
				if (normalizedTagList.get(i).equals("(")) {
					normalizedTagList.remove(i);
					correspondingOpeningParenthesisIndex = i;
					break;
				} else {
					expressionParenthesized.add(normalizedTagList.get(i));
					normalizedTagList.remove(i);
				}
			}
			Collections.reverse(expressionParenthesized);
			// we now work to suppress the group between those two indexes
			
			List<Set<Long>> setListedParenthesized = getSubListedSet(
					originalNormalizedTagList,
					questionsSetList,
					correspondingOpeningParenthesisIndex,
					firstClosingParenthesisIndex);
			
			reduceGroup(expressionParenthesized, setListedParenthesized);
			
			normalizedTagList.add(correspondingOpeningParenthesisIndex, "?");
		}
		
		return questionsSetList.get(0);
	}
	
	public List<Set<Long>> getSubListedSet(List<String> normalizedTagList,
			List<Set<Long>> questionsSetList, int start, int end) {
		int firstElementsCount = 0;
		int parenthesizedCount = 0;
		for (int i = 0; i < end; i++) {
			if (normalizedTagList.get(i).equals("?")) {
				if (i < start) {
					firstElementsCount++;
				} else {
					parenthesizedCount++;
				}
			}
		}
		
		return questionsSetList.subList(firstElementsCount, firstElementsCount
				+ parenthesizedCount);
	}
	
	// pre-condition: tagList does not contain parenthesis and is well-formed
	public Set<Long> reduceGroup(List<String> tagList, List<Set<Long>> questionsSetList) {
		while (tagList.contains("*")) {
			int firstANDIndex = tagList.indexOf("*");
			
			int leftOperandIndex = -1;
			for (int i = 0; i < firstANDIndex; i++) {
				if (tagList.get(i).equals("?")) {
					leftOperandIndex++;
				}
			}

			// compute the INTERSECTION of the two operands
			Set<Long> leftOperand = questionsSetList.get(leftOperandIndex);
			Set<Long> rightOperand = questionsSetList.remove(leftOperandIndex+1);
			leftOperand.retainAll(rightOperand);
			
			// update the tagList
			tagList.remove(firstANDIndex);
			tagList.remove(firstANDIndex); // this will remove the right-hand operand
		}
		

		while (tagList.contains("+")) {
			int firstANDIndex = tagList.indexOf("+");
			
			int leftOperandIndex = -1;
			for (int i = 0; i < firstANDIndex; i++) {
				if (tagList.get(i).equals("?")) {
					leftOperandIndex++;
				}
			}

			// compute the UNION of the two operands
			Set<Long> leftOperand = questionsSetList.get(leftOperandIndex);
			Set<Long> rightOperand = questionsSetList.remove(leftOperandIndex+1);
			leftOperand.addAll(rightOperand);
			
			// update the tagList
			tagList.remove(firstANDIndex);
			tagList.remove(firstANDIndex); // this will remove the right-hand operand
		}
		return questionsSetList.get(0);
	}
}
