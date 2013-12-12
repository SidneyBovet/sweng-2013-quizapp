package epfl.sweng.test.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.caching.OutboxManager;
import epfl.sweng.caching.SQLiteCacheHelper;
import epfl.sweng.quizquestions.QuizQuestion;

public class CacheContentProviderTest extends AndroidTestCase {

	private CacheContentProvider mProvider;
	private OutboxManager mOutbox;

	@Override
	protected void setUp() {

		// We work on a test DB.
		mProvider = new CacheContentProvider(true);
		mProvider.eraseDatabase();

		mOutbox = new OutboxManager();

		try {
			super.setUp();
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Problem when using" +
					"the super to set up the test", e);
			fail("Exception when setting up the test");
		}
	}

	@Override
	protected void tearDown() throws Exception {
		mProvider.close();
		mOutbox.close();
		super.tearDown();
	}

	public void testEraseDatabaseActuallyErasesIt() {
		mProvider.eraseDatabase();
		assertEquals(0, mOutbox.size());
	}

	public void testCanAddQuestion() {
		mProvider.addQuizQuestion(createFakeQuestion("lolilol"));

		Cursor cursor = mProvider.getQuestions(new QuizQuery());
		int id = cursor.getInt(cursor
				.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_PK));
		QuizQuestion question = mProvider.getQuestionFromPK(id);

		assertEquals("Statement should be the same.", "lolilol",
				question.getStatement());
	}

	public void testgetOutboxCountIsIncremented() {
		int expectedCount = mOutbox.size() + 1;
		long id = mProvider.addQuizQuestion(createFakeQuestion("lolilol"));
		mOutbox.push(id);
		assertEquals(expectedCount, mOutbox.size());
	}

	public void testWeDoNotTakeTheQuestionFromTheOutboxWhenPeeking() {
		assertEquals(0, mOutbox.size());
		long id = mProvider
				.addQuizQuestion(createFakeQuestion("MyNewQuestion"));
		mOutbox.push(id);
		assertEquals(1, mOutbox.size());
		mOutbox.peek();
		assertEquals(1, mOutbox.size());
	}

	public void testFullQuestion() {

		QuizQuestion expectedQuestion = createFakeFullQuestion("What's the answer?");

		long id = mProvider.addQuizQuestion(expectedQuestion);
		QuizQuestion cachedQuestion = mProvider.getQuestionFromPK(id);

		assertTrue(null != cachedQuestion);

		assertEquals(expectedQuestion.getId(), cachedQuestion.getId());
		assertEquals(expectedQuestion.getOwner(), cachedQuestion.getOwner());
		assertEquals(expectedQuestion.getSolutionIndex(),
				cachedQuestion.getSolutionIndex());
		assertEquals(expectedQuestion.getStatement(),
				cachedQuestion.getStatement());
		assertEquals(expectedQuestion.getTagsToString(),
				cachedQuestion.getTagsToString());
		assertEquals(expectedQuestion.getAnswers(), cachedQuestion.getAnswers());
	}

	public void testFullQuestionInCache() {

		QuizQuestion expectedquestion = createFakeFullQuestion("What's the answer?");

		long id = mProvider.addQuizQuestion(expectedquestion);
		mOutbox.push(id);
		QuizQuestion cachedQuestion = mOutbox.peek();

		assertEquals(expectedquestion.getId(), cachedQuestion.getId());
		assertEquals(expectedquestion.getOwner(), cachedQuestion.getOwner());
		assertEquals(expectedquestion.getSolutionIndex(),
				cachedQuestion.getSolutionIndex());
		assertEquals(expectedquestion.getStatement(),
				cachedQuestion.getStatement());
		assertEquals(expectedquestion.getTagsToString(),
				cachedQuestion.getTagsToString());
		assertEquals(expectedquestion.getAnswers(), cachedQuestion.getAnswers());
	}

	public void testQuestionQuerySuccessful() {
		QuizQuestion expectedQuestion = createFakeFullQuestion("Question statement");

		long expectedQuestionId = mProvider.addQuizQuestion(expectedQuestion);
		Cursor fetchedQuestionsCursor = mProvider.getQuestions(new QuizQuery(
				"Milionaire Funny", ""));

		if (null != fetchedQuestionsCursor
				&& fetchedQuestionsCursor.moveToFirst()) {
			long fetchedQuestionId = fetchedQuestionsCursor
					.getInt(fetchedQuestionsCursor
							.getColumnIndex(SQLiteCacheHelper.FIELD_QUESTIONS_PK));
			assertEquals(expectedQuestionId, fetchedQuestionId);
		} else {
			assertTrue(false);
		}
	}

	public void testOutboxWorksFIFO() {

		final int nbQuestions = 10;

		for (int i = 1; i <= nbQuestions; i++) {
			long id = mProvider.addQuizQuestion(createFakeQuestion("questionStatement"
					+ i));
			mOutbox.push(id);
		}

		for (int i = 1; i <= nbQuestions; i++) {
			assertEquals("questionStatement" + i, mOutbox.peek().getStatement());
			mOutbox.pop();
		}

		assertEquals(0, mOutbox.size());
	}

	/*********************** Private methods ***********************/

	private QuizQuestion createFakeQuestion(String questionStatement) {
		List<String> answers = new ArrayList<String>();
		answers.add("Answer1");
		answers.add("Answer2");
		answers.add("Answer3");
		answers.add("Obiwan Kenobi");

		Set<String> tags = new HashSet<String>();
		tags.add("Milionaire");
		tags.add("Funny");

		return new QuizQuestion(questionStatement, answers, 1, tags);
	}

	private QuizQuestion createFakeFullQuestion(String questionStatement) {

		List<String> answers = new ArrayList<String>();
		answers.add("Answer1");
		answers.add("Answer2");
		answers.add("Answer3");
		answers.add("Obiwan Kenobi");

		Set<String> tags = new HashSet<String>();
		tags.add("Milionaire");
		tags.add("Funny");

		int questionId = 25;
		String owner = "David";

		return new QuizQuestion(questionStatement, answers, 1, tags,
				questionId, owner);
	}
}
