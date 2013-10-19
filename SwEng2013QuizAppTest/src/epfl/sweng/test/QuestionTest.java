package epfl.sweng.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.backend.Converter;
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
		Question question = new Question(mQuestionID, mQuestionContent, 
				mQuestionAnswers, mQuestionSolutionIndex, mQuestionTagsSet,
				mQuestionOwner);
		assertEquals(question.getId(), mQuestionID);
		assertEquals(question.getQuestionContent(), 
				"Question: " + mQuestionContent);
		assertEquals(question.getAnswers().get(0), mQuestionAnswers.get(0));
		assertEquals(question.getSolutionIndex(), mQuestionSolutionIndex);
	}

	public void testJSONTranslation() {
		final Question question = new Question(mQuestionID, mQuestionContent, 
				mQuestionAnswers, mQuestionSolutionIndex, mQuestionTagsSet,
				mQuestionOwner);
		String jsonContent = "{"
				+ "\"id\": \"1\","
				+ "\"tags\": \"[tag1, tag2]\","
				+ "\"owner\": \"Anonymous\","
                + " \"answers\": \"[Answer1, Answer2, Answer3]\","
				+ "\"question\": \"content\","
                + " \"solutionIndex\": 2"
				+ "}";
		JSONObject json = null;
		try {
			json = new JSONObject(jsonContent);
			
			// Since JSON developers suck so bad, we cannot do an actual equals
			// between two JSON Objects, so we just compare them row by row.
			
			String[] tags = { "id", "owner", "answers", "question", "solutionIndex" };
			
			for (String tag : tags) {
				assertEquals(json.getString(tag), question.toJSON().getString(tag));
			}
			
		} catch (JSONException e) {
			fail("Exception when hard creating JSONobject");
			e.printStackTrace();
		}
	}

	public void testJSONToArray() {
		JSONArray jsonArray = new JSONArray();
		jsonArray.put("Answer1");
		jsonArray.put("Answer2");
		jsonArray.put("Answer3");
		assertEquals(mQuestionAnswers, Converter.jsonArrayToStringArray(jsonArray));
	}
	
	public void testTagToString() {
		//assertEquals("Tags: tag1, tag2", .getTagsToString(mQuestionTagsSet));
	}
}
