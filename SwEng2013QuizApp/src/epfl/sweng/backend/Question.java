package epfl.sweng.backend;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sweng.epfl.editquestions.QuizEditExecution;

import epfl.sweng.showquestions.DownloadJSONFromServer;

/**
 * Represents a question that a user gets asked in the quiz application.
 * 
 * @author born4new
 * 
 */
public class Question {
	private long id;
	private String questionContent;
	private ArrayList<String> answers;
	private int solutionIndex;
	private ArrayList<String> tags;
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
	public Question(long questionId, String questionStmt,
			ArrayList<String> questionAnswers, int questionSolutionIndex,
			ArrayList<String> questionTags, String questionOwner) {
		this.id = questionId;
		this.questionContent = questionStmt;
		this.answers = questionAnswers;
		this.solutionIndex = questionSolutionIndex;
		this.tags = questionTags;
		this.owner = questionOwner;
	}

	public Question(String questionStmt, ArrayList<String> questionAnswers,
			int questionSolutionIndex, ArrayList<String> questionTags) {
		this.questionContent = questionStmt;
		this.answers = questionAnswers;
		this.solutionIndex = questionSolutionIndex;
		this.tags = questionTags;
	}

	/**
	 * Processes a request in an {@link AsyncTask}.
	 * 
	 * @return The parsed question.
	 */
	public static Question getRandomQuestion() {
		DownloadJSONFromServer asyncTaskRandomQuestionGetter = new DownloadJSONFromServer();
		asyncTaskRandomQuestionGetter.execute();

		Question question = null;
		try {
			question = Question
					.createQuestionFromJSON(asyncTaskRandomQuestionGetter.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return question;
	}

	public static void submitRandomQuestion(ArrayList<String> listInputGUI) {
		Question questionToSubmit = createQuestionFromList(listInputGUI);
		JSONObject jsonToSubmit = createJSONFromQuestion(questionToSubmit);
		String test = "faux";
		try {
			 test = jsonToSubmit.get("question").toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String a = test;
		QuizEditExecution quizEditExecute = new QuizEditExecution();
		// TODO FINIR SUBMIT
		quizEditExecute.execute(jsonToSubmit);

	}

	public static Question createQuestionFromJSON(String questionJSON) {
		long id = -1;
		String question = null;
		JSONObject jsonParser = null;
		JSONArray answersJSON = null;
		ArrayList<String> answers = null;
		int solutionIndex = -1;
		JSONArray tagsJSON = null;
		ArrayList<String> tags = null;
		String owner = null;
		try {
			jsonParser = new JSONObject(questionJSON);
			id = jsonParser.getLong("id");
			question = jsonParser.getString("question");
			answersJSON = jsonParser.getJSONArray("answers");
			answers = getJSONArrayToStringArray(answersJSON);
			solutionIndex = jsonParser.getInt("solutionIndex");
			tagsJSON = jsonParser.getJSONArray("tags");
			tags = getJSONArrayToStringArray(tagsJSON);
			owner = jsonParser.getString("owner");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Question questionFromUser;
		if ((id == -1) || (owner == null)) {
			questionFromUser = new Question(question, answers, solutionIndex,
					tags);
		} else {
			questionFromUser = new Question(id, question, answers,
					solutionIndex, tags, owner);
		}

		return questionFromUser;
	}

	public static Question createQuestionFromList(ArrayList<String> listElm) {
		Question questionFromUser;
		int dummysolutionIndex = 1;
		String questionText = "\"" + listElm.remove(0) + "\"";
		String tagsInOneLine = listElm.remove(listElm.size() - 1);
		String formattedTags = tagsInOneLine.replaceAll("\\W", " ");
		String[] tagsInArray = formattedTags.split(" ");
		ArrayList<String> tagStrings = new ArrayList<String>();
		String temporaryTagsFormatted = null;
		for (int i = 0; i < tagsInArray.length; i++) {
			temporaryTagsFormatted = "\"" + tagsInArray[i] + "\"";
			tagStrings.add(temporaryTagsFormatted);
		}

		ArrayList<String> answers = new ArrayList<String>();
		String temporaryAnswerFormatted = null;
		for (int i = 0; i < listElm.size(); i++) {
			temporaryAnswerFormatted = "\"" + listElm.get(i) + "\"";
			answers.add(temporaryAnswerFormatted);
		}
		// XXX WHY DO THIS CREAT A QUESTION OBJECT WITH ID AND OWNER
		questionFromUser = new Question(questionText, answers,
				dummysolutionIndex, tagStrings);

		return questionFromUser;
	}

	public static JSONObject createJSONFromQuestion(Question question) {
		JSONObject jsonObject = new JSONObject();
		try {
			// XXX ! HERE I HAVE REMOVED ID AND OWNER SINCE THEY MUSTE BE IN THE
			// JSON OBJECT WHEN WE SEND AN EDITED QUESTION !
			jsonObject.put("question", question.questionContent);
			jsonObject.put("answers", question.answers);
			jsonObject.put("solutionIndex", question.solutionIndex);
			jsonObject.put("tags", question.tags);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObject;
	}

	/**
	 * Get one JSON object after the other, transform then to string and put
	 * them in an array list of string
	 * 
	 * @return an array list of string containing the JSONObjects
	 */
	private static ArrayList<String> getJSONArrayToStringArray(
			JSONArray arrayToChange) {
		ArrayList<String> list = new ArrayList<String>();
		if (arrayToChange != null) {
			for (int i = 0; i < arrayToChange.length(); i++) {
				if (arrayToChange.optString(i) != null) {
					list.add(arrayToChange.optString(i));
				}
			}
		}
		return list;
	}

	/**
	 * Returns all the tag together
	 * 
	 * @return all the tag delimited by ","
	 * @throws JSONException
	 */
	public String getTagsToString() throws JSONException {
		String tagsTogether = "tags: ";
		for (int i = 0; i < (tags.size()); i++) {
			tagsTogether += tags.get(i);
			if (i < tags.size() - 1) {
				tagsTogether += ", ";
			}
		}
		return tagsTogether;
	}

	public String getOwner() {
		return owner;
	}

	public int getSolutionIndex() {
		return solutionIndex;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public long getId() {
		return id;
	}

	public String getQuestionContent() {
		return "question:" + questionContent;
	}

	public ArrayList<String> getAnswers() {
		return answers;
	}

	@Override
	public String toString() {
		return "Question [id=" + id + ", questionContent=" + questionContent
				+ ", answers=" + answers.toString() + ", solutionIndex="
				+ solutionIndex + ", tags=" + tags.toString() + ", owner="
				+ owner + "]";
	}

}
