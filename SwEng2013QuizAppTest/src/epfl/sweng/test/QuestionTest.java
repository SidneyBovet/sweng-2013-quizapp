package epfl.sweng.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import junit.framework.TestCase;
import epfl.sweng.backend.Question;

/**
 * This test covers the Question class from the epfl.sweng.backend package.
 * 
 * @author Melody Lucid
 *
 */
public class QuestionTest extends TestCase {

	private final long mQuestionID = 1337L;
	private final String mQuestionContent = "Has anyone really been far even as "
			 + "decided to use even go want to do look more like?";
	private final ArrayList<String> mQuestionAnswers = new ArrayList<String>(
			Arrays.asList("Excuse me, what?", "Obiwan Kenobi", "No, no one has "
					+ "really even far to go want look more like that.",
					"The problem is NP-complete"));
	private final int mQuestionSolutionIndex = 2;
	private final HashSet<String> mQuestionTagsSet = new HashSet<String>(
			Arrays.asList("wtf", "4chan","copypasta"));
	private final String mQuestionOwner = "Anonymous";
	
	@Override
	public void setUp() {
		
	}
	
	public void testQuestionCreation() {
		final Question question = new Question(mQuestionID, mQuestionContent, 
				mQuestionAnswers, mQuestionSolutionIndex, mQuestionTagsSet,
				mQuestionOwner);
		assertEquals(question.getId(), mQuestionID);
		assertEquals(question.getQuestionContent(), mQuestionContent);
		assertEquals(question.getAnswers().get(2),mQuestionAnswers.get(2));
		assertEquals(question.getSolutionIndex(), mQuestionSolutionIndex);
	}
	
	public void testJSONTranslation() {
		
	}
}
