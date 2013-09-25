package epfl.sweng.backend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a question that a user gets asked in the quiz application.
 * 
 * @author born4new
 * 
 */
public class Question {
	private long id;
	private String questionContent;
	private JSONArray answers;
	private int solutionIndex;
	private JSONArray tags;
	private String owner;

	/**
	 * 
	 * @param id
	 *            : long that uniquely identifies a question.
	 * @param question
	 *            : String that contains the text of the question.
	 * @param answers
	 *            : List of String objects that contains the possible answers.
	 *            The list is 0-indexed, meaning that the first element's index
	 *            is 0. The order of the answers is important, because the
	 *            correct answer is identified by its index in the answers list.
	 * @param solutionIndex
	 *            : Int that indicates the correct answer by its position in the
	 *            answers list.
	 * @param tags
	 *            : Set of question tags.
	 * @param owner
	 *            : Question owner.
	 */
	public Question(
			long questionId,
			String questionStmt,
			JSONArray questionAnswers,
			int questionSolutionIndex,
			JSONArray questionTags,
			String questionOwner) {
		this.id = questionId;
		this.questionContent = questionStmt;
		this.answers = questionAnswers;
		this.solutionIndex = questionSolutionIndex;
		this.tags = questionTags;
		this.owner = questionOwner;
	}

	public static Question createQuestionFromJSON(String questionJSON)
		throws JSONException {
		
		JSONObject jsonParser = new JSONObject(questionJSON);
		long id = jsonParser.getLong("id");
		
		
		
		String question = jsonParser.getString("question");
		JSONArray answers = jsonParser.getJSONArray("answers");
		int solutionIndex = jsonParser.getInt("solutionIndex");
		JSONArray tags = jsonParser.getJSONArray("tags");
		String owner = jsonParser.getString("owner");

		return new Question(id, question, answers, solutionIndex, tags, owner);
	}

	public long getId() {
		return id;
	}

	public String getQuestionContent() {
		return "question:" + questionContent;
	}

	public JSONArray getAnswers() {
		return answers;
	}

	public int getSolutionIndex() {
		return solutionIndex;
	}

	public JSONArray getTags() {
		return tags;
	}
	
	/**
	 * Returns a single tag
	 * @param index
	 * @return The i-th tag of the Question object
	 * @throws JSONException
	 */
	public String getTag(int index) throws JSONException {
		return tags.getString(index);
	}
	


	public String getOwner() {
		return owner;
	}
	

	@Override
	public String toString() {
		return "Question [id=" + id + ", questionContent=" + questionContent
				+ ", answers=" + answers.toString() + ", solutionIndex=" + solutionIndex
				+ ", tags=" + tags.toString() + ", owner=" + owner + "]";
	}
}
