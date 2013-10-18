package epfl.sweng.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import junit.framework.TestCase;
import epfl.sweng.backend.Question;

/**
 * This test covers the Question class from the epfl.sweng.backend package.
 * 
 * @author Melody Lucid and JoTearoom
 *
 */
public class QuestionTest extends TestCase {

	private final long mQuestionID = (long) 1;
	private final String mQuestionContent = "content";
	private final ArrayList<String> mQuestionAnswers = new ArrayList<String>(
			Arrays.asList("Answer1", "Answer2", "Answer3"));
	private final int mQuestionSolutionIndex = 2;
	private final HashSet<String> mQuestionTagsSet = new HashSet<String>(
			Arrays.asList("tag1", "tag2"));
	private final String mQuestionOwner = "Anonymous";
	
	@Override
	public void setUp() {
		
	}
	
	public void testQuestionCreation() {
		final Question question = new Question(mQuestionID, mQuestionContent, 
				mQuestionAnswers, mQuestionSolutionIndex, mQuestionTagsSet,
				mQuestionOwner);
		assertEquals(question.getId(), mQuestionID);
		assertEquals(question.getQuestionContent(), 
				"Question: " + mQuestionContent);
		assertEquals(question.getAnswers().get(0), mQuestionAnswers.get(0));
		assertEquals(question.getSolutionIndex(), mQuestionSolutionIndex);
	}

	//TODO why not the same?!?
	/*
	public void testJSONTranslation() {
		final Question question = new Question(mQuestionID, mQuestionContent, 
				mQuestionAnswers, mQuestionSolutionIndex, mQuestionTagsSet,
				mQuestionOwner);
		String jsonContent = "{\"question\": \"content\","
                + " \"answers\": \"[Answer1,Answer2,Answer3]\", "
				+ "\"owner\": \"Anonymous\","
                + " \"solutionIndex\": 2, \"tags\": \"[tag1,tag2]\", "+
				"\"id\": \"1\" }";
		JSONObject json = null;
		try {
			json = new JSONObject(jsonContent);
		} catch (JSONException e) {
			fail("Exception when hard creating JSONobject");
			e.printStackTrace();
		}
		try {
			assertEquals(json.get("answers"),
				Question.createJSONFromQuestion(question).get("answers"));
				//or?
			assertEquals(json.get("answers"),
				question.toJSON.get("answers"));
		} catch (JSONException e) {
			fail("Exception when comparing one tag in the two JSONobject");
			e.printStackTrace();
		}	
	}*/
	
	//XXX difference toJSON CreateJSONFromQuestion??
	
	//XXX no view in LogCat of the log, hard to debug
	/*
	public void testQuestionTranslation() {
		final Question question = new Question(mQuestionID, mQuestionContent, 
				mQuestionAnswers, mQuestionSolutionIndex, mQuestionTagsSet,
				mQuestionOwner);
		String jsonContent = "{\"question\": \"content\","
                + " \"answers\": \"[Answer1,Answer2,Answer3]\", "
				+ "\"owner\": \"Anonymous\","
                + " \"solutionIndex\": 2, \"tags\": \"[tag1,tag2]\", "+
				"\"id\": \"1\" }";
		try {
			Log.v("question toString", question.toString());
			Log.v("qJson toString", Question.createQuestionFromJSON(jsonContent).toString());
			//assertEquals(question.toString(), Question.createQuestionFromJSON(jsonContent));
		} catch (JSONException e) {
			fail("Exception when comparing one tag in the two JSONobject");
			e.printStackTrace();
		}
	
	}*/
	
	//XXX getTagsToString in static??
	/*
	public void testTagToString() {
		assertEquals("Tags: tag1, tag2", Question.getTagsToString(mQuestionTagsSet));
	}*/
	
	
}
