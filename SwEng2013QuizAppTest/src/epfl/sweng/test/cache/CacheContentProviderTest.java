package epfl.sweng.test.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.database.Cursor;
import android.test.AndroidTestCase;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.caching.SQLiteCacheHelper;
import epfl.sweng.quizquestions.QuizQuestion;

public class CacheContentProviderTest extends AndroidTestCase {

	private CacheContentProvider mProvider;

	@Override
	protected void setUp() {

		// We work on a test DB.
		mProvider = new CacheContentProvider(true);

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

	public void testWeDoNotTakeTheQuestionFromTheOutboxWhenPeeking() {
		assertEquals(0, mProvider.getOutboxCount());
		mProvider.addQuizQuestion(createFakeQuestion("MyNewQuestion"), true);
		assertEquals(1, mProvider.getOutboxCount());
		mProvider.peekFirstQuestionFromOutbox();
		assertEquals(1, mProvider.getOutboxCount());
	}

	public void addingQuizQuestionDoesNotInfluenceTheOutbox() {
		assertEquals(0, mProvider.getOutboxCount());
		mProvider.addQuizQuestion(createFakeQuestion("MyNewQuestion"), true);
		assertEquals(0, mProvider.getOutboxCount());
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

		mProvider.addQuizQuestion(expectedquestion, true);
		QuizQuestion cachedQuestion = mProvider.peekFirstQuestionFromOutbox();

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
			mProvider.addQuizQuestion(createFakeQuestion("questionStatement"
					+ i), true);
		}

		for (int i = 1; i <= nbQuestions; i++) {
			assertEquals("questionStatement" + i, mProvider
					.peekFirstQuestionFromOutbox().getStatement());

			mProvider.takeFirstQuestionFromOutbox();
		}
		
		assertEquals(0, mProvider.getOutboxCount());
	}
	
	public void testReduceGroupWorks() {
		// tests "?+?*?" and {[1,2],[3,4],[3,5]}
		// (should return [1,2,3])
		String[] tagArray = {"?","+","?","*","?"};
		List<String> tagList = new ArrayList<String>(Arrays.asList(tagArray));

		Long[] group1 = {Long.valueOf(1),Long.valueOf(2)};
		List<Long> group1List = new ArrayList<Long>(Arrays.asList(group1));
		Set<Long> group1Set = new HashSet<Long>(group1List);
		Long[] group2 = {Long.valueOf(3),Long.valueOf(4)};
		List<Long> group2List = new ArrayList<Long>(Arrays.asList(group2));
		Set<Long> group2Set = new HashSet<Long>(group2List);
		Long[] group3 = {Long.valueOf(3),Long.valueOf(5)};
		List<Long> group3List = new ArrayList<Long>(Arrays.asList(group3));
		Set<Long> group3Set = new HashSet<Long>(group3List);
		List<Set<Long>> questionsSetList = new ArrayList<Set<Long>>();
		questionsSetList.add(group1Set);
		questionsSetList.add(group2Set);
		questionsSetList.add(group3Set);
		
		
		Long[] groupExpected = {Long.valueOf(1),Long.valueOf(2),Long.valueOf(3)};
		Set<Long> expected = new HashSet<Long>(Arrays.asList(groupExpected));
		Set<Long> provided = mProvider.reduceGroup(tagList, questionsSetList);
		assertEquals(expected, provided);
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
