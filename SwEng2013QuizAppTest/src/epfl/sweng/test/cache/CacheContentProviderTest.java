package epfl.sweng.test.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import epfl.sweng.caching.CacheContentProvider;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.test.activities.GUITest;


public class CacheContentProviderTest extends GUITest<MainActivity> {
	private CacheContentProvider mProvider;

	public CacheContentProviderTest() {
		super(MainActivity.class);
	}
	
	@Override
	protected void setUp() {
		mProvider = new CacheContentProvider(
				getInstrumentation().getTargetContext(), true);
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		//XXX how to clean? working?
		mProvider.eraseDatabase();
		mProvider.destroy();
		super.tearDown();
	}

	public void testCanAddQuestion() {
		mProvider.addQuizQuestion(createFakeQuestion("lolilol"));
		
		QuizQuestion question = mProvider.getRandomQuestion();
		
		getSolo().sleep(500);
		assertEquals("Statement should be the same.",
				"lolilol", question.getStatement());
	}

	private QuizQuestion createFakeQuestion(String questionStatement) {
		List<String> answers = new ArrayList<String>();
		answers.add("100% accurate");
		answers.add("Fully voodoo and could generate non-pseudorandom numbers");
		
		Set<String> tags = new HashSet<String>();
		tags.add("robotium");
		tags.add("testing");
		
		QuizQuestion question = new QuizQuestion(
				questionStatement, answers, 1, tags);
		return question;
	}
}