package epfl.sweng.test.proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.quizquestions.QuizQuestion;

public class QuestionProxyTest extends TestCase{	
	private QuestionsProxy proxy;
	private QuizQuestion mQuestion;
	
	@Override
	protected void setUp() throws Exception {
		proxy = QuestionsProxy.getInstance();
		mQuestion = new QuizQuestion("q", 
				new ArrayList<String>(Arrays.asList("a1", "a2", "a3")), 
				0, new TreeSet<String>(Arrays.asList("t1", "t2")),1, "o");
	}
	
	public void testSingleton() {
		QuestionsProxy proxy2 = QuestionsProxy.getInstance();
		assertTrue(proxy.equals(proxy2));
	} 

}
