package epfl.sweng.test.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.caching.SQLiteCacheHelper;
import epfl.sweng.quizquestions.QuizQuestion;

public class CacheContentProviderTest extends AndroidTestCase {

	private CacheContentProvider mProvider;

	@Override
	protected void setUp() {

		// We work on a test DB.
		RenamingDelegatingContext context = new RenamingDelegatingContext(
				getContext(), "test_");
		mProvider = new CacheContentProvider(context, true);

		try {
			super.setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void tearDown() throws Exception {
		mProvider.close();
		super.tearDown();
	}

	public void testEraseDatabaseActuallyErasesIt() {
		mProvider.eraseDatabase();
		assertEquals(0, mProvider.getOutboxCount());
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
		int expectedCount = mProvider.getOutboxCount() + 1;
		mProvider.addQuizQuestion(createFakeQuestion("lolilol"), true);
		assertEquals(expectedCount, mProvider.getOutboxCount());
	}

	public void testWeDoNotRemoveTheQuestionFromTheOutboxWhenGettingIt() {
		assertEquals(0, mProvider.getOutboxCount());
		mProvider.addQuizQuestion(createFakeQuestion("MyNewQuestion"), true);
		assertEquals(1, mProvider.getOutboxCount());
		mProvider.getFirstQuestionFromOutbox();
		assertEquals(1, mProvider.getOutboxCount());
	}

	public void testOutboxWorksFIFO() {

		final int nbQuestions = 10;

		for (int i = 1; i <= nbQuestions; i++) {
			mProvider.addQuizQuestion(createFakeQuestion("questionStatement"
					+ i), true);
		}

		for (int i = 1; i <= nbQuestions; i++) {
			assertEquals("questionStatement" + i, mProvider
					.getFirstQuestionFromOutbox().getStatement());

			mProvider.takeFirstQuestionFromOutbox();
		}
	}

	/*********************** Private methods ***********************/

	private QuizQuestion createFakeQuestion(String questionStatement) {
		List<String> answers = new ArrayList<String>();
		answers.add("100% accurate");
		answers.add("Fully voodoo and could generate non-pseudorandom numbers");

		Set<String> tags = new HashSet<String>();
		tags.add("robotium");
		tags.add("testing");

		QuizQuestion question = new QuizQuestion(questionStatement, answers, 1,
				tags);
		return question;
	}
}
