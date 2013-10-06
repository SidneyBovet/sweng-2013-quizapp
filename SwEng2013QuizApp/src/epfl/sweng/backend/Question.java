package epfl.sweng.backend;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data structure of a question for the quiz application.
 *  
 * @author born4new
 * 
 */
public class Question {
	
	private long mId;
	private String mQuestionContent;
	private List<String> mAnswers;
	private int mSolutionIndex;
	private List<String> mTags;
	private String mOwner;
	
	/**
	 * Constructor
	 * 
	 * @param id
	 *            Unique identifier of the question
	 * @param content
	 *            Text body of the question
	 * @param answers
	 *            List of all possible answers. The list is 0-indexed, meaning
	 *            that the first element's index is 0. The order matters,
	 *            because the correct one is identified by its index.
	 * @param solutionIndex
	 *            Correct answer index in the answers list.
	 * @param tags
	 *            Set of question tags.
	 * @param owner
	 *            Question owner.
	 */
	
	public Question(long id, String content,
			List<String> answers, int solutionIndex,
			List<String> tags, String owner) {
		this.mId = id;
		this.mQuestionContent = content;
		this.mAnswers = answers;
		this.mSolutionIndex = solutionIndex;
		this.mTags = tags;
		this.mOwner = owner;
	}
	
	/**
	 * Constructor without identity nor owner.
	 * 
	 * @param content
	 *            Text body of the question
	 * @param answers
	 *            List of all possible answers. The list is 0-indexed, meaning
	 *            that the first element's index is 0. The order matters,
	 *            because the correct one is identified by its index.
	 * @param solutionIndex
	 *            Correct answer index in the answers list.
	 * @param tags
	 *            Set of question tags.
	 */
	
	public Question(String content, List<String> answers,
			int solutionIndex, List<String> tags) {
		this(-1, content, answers, solutionIndex,
				tags, null);
	}
	
	/**
	 * Returns a {@link JSONObject} containing the data of the question.
	 * 
	 * @return A JSONObject of the question.
	 */
	
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("id", mId);
			jsonObject.put("question", mQuestionContent);
			jsonObject.put("answers", mAnswers);
			jsonObject.put("solutionIndex", mSolutionIndex);
			jsonObject.put("tags", mTags);
			jsonObject.put("owner", mOwner);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}
	
	/**
	 * Returns all the tags in a single <code>String</code>, delimited by a
	 * comma.
	 * 
	 * @return A printable representation of all the tags.
	 * @throws JSONException
	 */
	
	public String getTagsToString() throws JSONException {
		String tagsTogether = "Tags: ";
		for (int i = 0; i < mTags.size(); i++) {
			tagsTogether += mTags.get(i);
			if (i < mTags.size() - 1) {
				tagsTogether += ", ";
			}
		}
		return tagsTogether;
	}
	
	/**
	 * Returns the owner field of the question.
	 * 
	 * @return owner of the question.
	 */
	
	public String getOwner() {
		return mOwner;
	}
	
	/**
	 * Returns the solution index of the question.
	 * 
	 * @return solution index of the question.
	 */
	
	public int getSolutionIndex() {
		return mSolutionIndex;
	}
	
	/**
	 * Returns the list of tags of the question.
	 * 
	 * @return list of tags of the question.
	 */
	
	public List<String> getTags() {
		return mTags;
	}
	
	/**
	 * Returns the id of the question.
	 * 
	 * @return id of the question.
	 */
	
	public long getId() {
		return mId;
	}
	
	/**
	 * Returns the question text body.
	 * 
	 * @return question text body.
	 */
	
	public String getQuestionContent() {
		return "Question: " + mQuestionContent;
	}
	
	/**
	 * Returns the list of answers for the question.
	 * 
	 * @return list of answers for the question.
	 */
	
	public List<String> getAnswers() {
		return mAnswers;
	}
	
	/**
	 * Returns a printable version of the question.
	 */
	
	@Override
	public String toString() {
		return "Question [id=" + mId + ", questionContent=" + mQuestionContent
				+ ", answers=" + mAnswers.toString() + ", solutionIndex="
				+ mSolutionIndex + ", tags=" + mTags.toString() + ", owner="
				+ mOwner + "]";
	}
	
	/**
	 * Creates an instance of <code>Question</code> from a string field of a
	 * {@link JSONObject}, then returns it.
	 * 
	 * @param questionJSON The {@link JSONObject} string field to convert.
	 * @return A <code>Question</code> instance created from a {@link JSONObject} 
	 *            string field
	 * @throws JSONException
	 */
	
	public static Question createQuestionFromJSON(String questionJSON)
		throws JSONException {
		
		JSONObject jsonParser = new JSONObject(questionJSON);
		long id = jsonParser.getLong("id");
		
		String question = jsonParser.getString("question");
		JSONArray answersJSON = jsonParser.getJSONArray("answers");
		List<String> answers = JSONArrayToStringArray(answersJSON);
		
		int solutionIndex = jsonParser.getInt("solutionIndex");
		JSONArray tagsJSON = jsonParser.getJSONArray("tags");
		List<String> tags = JSONArrayToStringArray(tagsJSON);
		String owner = jsonParser.getString("owner");
		
		return new Question(id, question, answers, solutionIndex, tags, owner);
	}
	
	/**
	 * Creates an instance of <code>Question</code> from a list of elements,
	 * containing the question text body, the tags, the solution index, and the
	 * answers, then returns it.
	 * 
	 * @param listElm A list of the question data fields.
	 * @return A <code>Question</code> instance created from a list of fields.
	 */
	
	public static Question createQuestionFromList(List<String> listElm) {
		String questionText = listElm.remove(0);
		String tagsInOneLine = listElm.remove(listElm.size() - 1);
		int solutionIndex = Integer
				.parseInt(listElm.remove(listElm.size() - 1));
		String formattedTags = tagsInOneLine.replaceAll("(\\s*)\\W+(\\s*)", " ");
		String[] tagsInArray = formattedTags.split(" ");
		
		List<String> tagStrings = new ArrayList<String>();
		for (String tag : tagsInArray) {
			tagStrings.add(tag);
		}
		
		List<String> answers = new ArrayList<String>();
		for (String answer : listElm) {
			answers.add(answer);
		}
		
		return new Question(questionText, answers, solutionIndex, tagStrings);
	}

	/**
	 * Creates a {@link JSONObject} from an instance of question.
	 * 
	 * @param question The question to convert
	 * @return A {@link JSONObject} instance created from a question.
	 */
	
	public static JSONObject createJSONFromQuestion(Question question) {
		JSONObject questionIntoJSON = new JSONObject();
		JSONArray answersJSON = new JSONArray();
		for (int i = 0; i < question.mAnswers.size(); i++) {
			answersJSON.put(question.mAnswers.get(i));
		}
		JSONArray tagsJSON = new JSONArray();
		for (int i = 0; i < question.mTags.size(); i++) {
			tagsJSON.put(question.mTags.get(i));
		}
		try {
			questionIntoJSON.put("question", question.mQuestionContent);
			questionIntoJSON.put("answers", answersJSON);
			questionIntoJSON.put("solutionIndex", question.mSolutionIndex);
			questionIntoJSON.put("tags", tagsJSON);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return questionIntoJSON;
	}
	
	/**
	 * Gets a list of converted {@link JSONObject} into <code>String</code>,
	 * retrieved from a {@link JSONArray}.
	 * 
	 * @param arrayToConvert The {@link JSONArray} to convert.
	 * @return An <code>ArrayList</code> of the <code>JSONObjects</code> string
	 *            field.
	 */
	// TODO refactor name
	private static List<String> JSONArrayToStringArray(
			JSONArray arrayToConvert) {
		ArrayList<String> list = new ArrayList<String>();
		if (arrayToConvert != null) {
			for (int i = 0; i < arrayToConvert.length(); i++) {
				if (arrayToConvert.optString(i) != null) {
					list.add(arrayToConvert.optString(i));
				}
			}
		}
		return list;
	}
}
