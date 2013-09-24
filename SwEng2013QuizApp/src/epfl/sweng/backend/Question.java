package epfl.sweng.backend;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a question that a user gets asked in the quizz application.
 * @author born4new
 *
 */
public class Question {
	
	private int id;
	private String questionContent;
	private String[] answers;
	private int solutionIndex;
	private String[] tags;
	private String owner;
	
	// XXX : Should we comment the constructor here since the variable names are quite self-explanatory?
	/**
	 * 
	 * @param id 				: long that uniquely identifies a question.
	 * @param question 			: String that contains the text of the question.
	 * @param answers			: List of String objects that contains the possible answers. 
	 * 							  The list is 0-indexed, meaning that the first element�s index is 0.
	 * 							  The order of the answers is important, because the correct answer is 
	 * 							  identified by its index in the answers list.
	 * @param solutionIndex		: Int that indicates the correct answer by its position in the answers list.
	 * @param tags				: Set of question tags.
	 * @param owner				: Question owner.
	 */
	public Question(int id, 
					String question, 
					String[] answers, 
					int solutionIndex, 
					String[] tags, 
					String owner) {
		this.id = id;
		this.questionContent = question;
		this.answers = answers;
		this.solutionIndex = solutionIndex;
		this.tags = tags;
		this.owner = owner;
	}

	public static Question createQuestionFromJSON(String questionJSON) throws JSONException {
		
		// JSON Parsing.
		int id = 0;
		String question = "";
		String[] answers = {};
		int solutionIndex = 0;
		String[] tags = {};
		String owner = "";

		JSONObject jsonParser = new JSONObject(questionJSON);
		id = (Integer) jsonParser.get("id");
		question = (String) jsonParser.get("question");
		answers = (String[]) jsonParser.get("answers");
		solutionIndex = Integer.parseInt((String) jsonParser.get("solutionIndex"));
		tags = (String[]) jsonParser.get("tags");
		owner = (String) jsonParser.get("owner");
		
		return new Question(id, question, answers, solutionIndex, tags, owner);
	}
	
	public int getId() {
		return id;
	}

	public String getQuestionContent() {
		return questionContent;
	}

	public String[] getAnswers() {
		return answers;
	}

	public int getSolutionIndex() {
		return solutionIndex;
	}

	public String[] getTags() {
		return tags;
	}

	public String getOwner() {
		return owner;
	}
}