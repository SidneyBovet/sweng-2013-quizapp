package epfl.sweng.test.proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;

import junit.framework.TestCase;
import epfl.sweng.entry.MainActivity;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.test.activities.GUITest;

public class QuestionProxyTest extends GUITest<MainActivity>{	
	
	public QuestionProxyTest() {
		super(MainActivity.class);
	}

	private QuestionsProxy proxy;
	private QuizQuestion mQuestion;
	private Context contextOfMainActivity;
	
	@Override
	protected void setUp() {
		super.setUp();
		contextOfMainActivity = getInstrumentation()
				.getTargetContext();
		proxy = QuestionsProxy.getInstance(contextOfMainActivity);
		mQuestion = new QuizQuestion("q", 
				new ArrayList<String>(Arrays.asList("a1", "a2", "a3")), 
				0, new TreeSet<String>(Arrays.asList("t1", "t2")), 1, "o");
	}
	
	public void testSingleton() {
		QuestionsProxy proxy2 = QuestionsProxy.getInstance(contextOfMainActivity);
		assertTrue(proxy.equals(proxy2));
	} 

}
