package epfl.sweng.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sweng.epfl.editquestions.QuizEditExecution;
import android.os.AsyncTask;
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
	private List<String> answers;
	private int solutionIndex;
	private List<String> tags;
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
			List<String> questionAnswers, int questionSolutionIndex,
			List<String> questionTags, String questionOwner) {
		this.id = questionId;
		this.questionContent = questionStmt;
		this.answers = questionAnswers;
		this.solutionIndex = questionSolutionIndex;
		this.tags = questionTags;
		this.owner = questionOwner;
	}

	/**
	 * Alternative constructor for sending a question (we don't have questionId nor owner)
	 * @param questionStmt
	 * @param questionAnswers
	 * @param questionSolutionIndex
	 * @param questionTags
	 */
	public Question(String questionStmt, List<String> questionAnswers,
			int questionSolutionIndex, List<String> questionTags) {
		this(-1, questionStmt, questionAnswers, questionSolutionIndex, questionTags, null);
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
			question = createQuestionFromJSON(asyncTaskRandomQuestionGetter.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return question;
	}

	public static void submitQuestion(List<String> listInputGUI) {
		Question questionToSubmit = createQuestionFromList(listInputGUI);
		JSONObject jsonToSubmit = questionToSubmit.toJSON();
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
		
		return new Question(id, question, answers, solutionIndex,
				tags, owner);
	}

	public static Question createQuestionFromList(List<String> listElm) {
		
		int dummysolutionIndex = 1;
		String questionText = listElm.remove(0);
		String tagsInOneLine = listElm.remove(listElm.size() - 1);
		String formattedTags = tagsInOneLine.replaceAll("\\W", " ");
		String[] tagsInArray = formattedTags.split(" ");
		
		List<String> tagStrings = new ArrayList<String>();		
		for (String tag : tagsInArray) {
			tagStrings.add(tag);
		}

		List<String> answers = new ArrayList<String>();
		for (String answer : listElm) {
			answers.add(answer);
		}
		
		return new Question(questionText, answers,
				dummysolutionIndex, tagStrings);
	}
	
	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		try {
			JSONArray answersJSONArray = new JSONArray(answers);
			JSONArray tafsJSONArray = new JSONArray(tags);
			jsonObject.put("question", questionContent);
			jsonObject.put("answers", answersJSONArray);
			jsonObject.put("solutionIndex", solutionIndex);
			jsonObject.put("tags", tafsJSONArray);

		} catch (JSONException e) {
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

	public List<String> getTags() {
		return tags;
	}

	public long getId() {
		return id;
	}

	public String getQuestionContent() {
		return "question:" + questionContent;
	}

	public List<String> getAnswers() {
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
