package epfl.sweng.backend;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a question that a user gets asked in the quizz application.
 * 
 * @author born4new
 * 
 */
public class Question {
<<<<<<< HEAD
	
=======

>>>>>>> b617fb33fcd79d60a45f67126175b2dc04287de4
	private long id;
	private String questionContent;
	private String[] answers;
	private int solutionIndex;
	private String[] tags;
	private String owner;

	/**
	 * 
<<<<<<< HEAD
	 * @param id 				: long that uniquely identifies a question.
	 * @param question 			: String that contains the text of the question.
	 * @param answers			: List of String objects that contains the possible answers. 
	 * 							  The list is 0-indexed, meaning that the first elementï¿½s index is 0.
	 * 							  The order of the answers is important, because the correct answer is 
	 * 							  identified by its index in the answers list.
	 * @param solutionIndex		: Int that indicates the correct answer by its position in the answers list.
	 * @param tags				: Set of question tags.
	 * @param owner				: Question owner.
	 */
	public Question(long id, 
					String question, 
					String[] answers, 
					int solutionIndex, 
					String[] tags, 
					String owner) {
=======
	 * @param id
	 *            : long that uniquely identifies a question.
	 * @param question
	 *            : String that contains the text of the question.
	 * @param answers
	 *            : List of String objects that contains the possible answers.
	 *            The list is 0-indexed, meaning that the first elementÕs index
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
	public Question(long id, String question, String[] answers,
			int solutionIndex, String[] tags, String owner) {
>>>>>>> b617fb33fcd79d60a45f67126175b2dc04287de4
		this.id = id;
		this.questionContent = question;
		this.answers = answers;
		this.solutionIndex = solutionIndex;
		this.tags = tags;
		this.owner = owner;
	}

	public static Question createQuestionFromJSON(String questionJSON)
			throws JSONException {

		JSONObject jsonParser = new JSONObject(questionJSON);
<<<<<<< HEAD
=======
		
>>>>>>> b617fb33fcd79d60a45f67126175b2dc04287de4
		long id = Long.parseLong((String) jsonParser.get("id"));
		String question = (String) jsonParser.get("question");
		String[] answers = (String[]) jsonParser.get("answers");
		int solutionIndex = Integer.parseInt((String) jsonParser
				.get("solutionIndex"));
		String[] tags = (String[]) jsonParser.get("tags");
		String owner = (String) jsonParser.get("owner");

		return new Question(id, question, answers, solutionIndex, tags, owner);
	}
<<<<<<< HEAD
	
=======

>>>>>>> b617fb33fcd79d60a45f67126175b2dc04287de4
	public long getId() {
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
