package epfl.sweng.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.quizquestions.Converter;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * This test covers the Question class from the epfl.sweng.backend package.
 * 
 * @author Melody Lucid and JoTearoom
 *
 */
public class QuestionTest extends TestCase {

	private final int mQuestionID = 1;
	private final String mQuestionContent = "content";
	private final ArrayList<String> mQuestionAnswers = new ArrayList<String>(
			Arrays.asList("Answer1", "Answer2", "Answer3"));
	private final int mQuestionSolutionIndex = 2;
	private final Set<String> mQuestionTagsSet = new TreeSet<String>(
			Arrays.asList("tag1", "tag2"));
	private final String mQuestionOwner = "Anonymous";
	private QuizQuestion mQuestion;
	private QuizQuestion mQuestion2;
	
	@Override
	public void setUp() {
		mQuestion = new QuizQuestion(mQuestionContent, 
				mQuestionAnswers, mQuestionSolutionIndex, mQuestionTagsSet,
				mQuestionID, mQuestionOwner);
		mQuestion2 = new QuizQuestion(mQuestionContent, 
				mQuestionAnswers, mQuestionSolutionIndex, mQuestionTagsSet);
	}
	
	public void testQuestionCreation() {
		assertEquals(mQuestion.getId(), mQuestionID);
		assertEquals(mQuestion.getQuestionContent(), 
				"Question: " + mQuestionContent);
		assertEquals(mQuestion.getAnswers().get(0), mQuestionAnswers.get(0));
		assertEquals(mQuestion.getSolutionIndex(), mQuestionSolutionIndex);
	}

	public void testJSONTranslation() {
		String jsonContent = "{"
				+ "\"id\": \"1\","
				+ "\"tags\": \"[tag1, tag2]\","
				+ "\"owner\": \"Anonymous\","
                + " \"answers\": [\"Answer1\", \"Answer2\", \"Answer3\"],"
				+ "\"question\": \"content\","
                + " \"solutionIndex\": 2"
				+ "}";
		JSONObject json = null;
		try {
			json = new JSONObject(jsonContent);
			
			// Since JSON developers suck so bad, we cannot do an actual equals
			// between two JSON Objects, so we just compare them row by row.
			
			String[] tags = {"id", "owner", "answers", "question", "solutionIndex"};
			
			for (String tag : tags) {
				assertEquals(json.getString(tag), mQuestion.toJSON().getString(tag));
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
	
	// Since the tags are in the set the order here can be changed
	public void testTagToString() {
		assertEquals("Tags: tag1, tag2", mQuestion.getTagsToString());
	}
	
	public void testJSONToSet() {
		JSONArray jsonArray = new JSONArray();
		jsonArray.put("tag1");
		jsonArray.put("tag2");
		assertEquals(mQuestionTagsSet, 
				Converter.jsonArrayToStringSet(jsonArray));
	}
	
	public void testToString() {
		assertEquals(mQuestion.toString(), "Question [id=1, questionContent=" +
				"content, answers=[Answer1, Answer2, Answer3], solutionIndex=2, " +
				"tags=[tag1, tag2], owner=Anonymous]");
	}
	
	public void testSecondConstructor() {
		assertEquals(-1, mQuestion2.getId());
		assertEquals(null, mQuestion2.getOwner());
	}
	
	public void testListTranslation() {
		List<String> listToQuestion = new ArrayList<String>();
		listToQuestion.add(mQuestionContent);

		List<String> listAnswers = mQuestionAnswers;
		listToQuestion.addAll(listAnswers);

		int indexGoodAnswer = mQuestionSolutionIndex;
		String indexGoodAnswerString = Integer.toString(indexGoodAnswer);

		listToQuestion.add(indexGoodAnswerString);
		listToQuestion.add("tag1, tag2");
		//TODO check why if we compare the two objects the tags are switched??
		assertEquals(mQuestion2.getAnswers(), 
				QuizQuestion.createQuestionFromList(listToQuestion).getAnswers());
	}	
}
