package epfl.sweng.backend;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return question;
	}

	public static Question createQuestionFromJSON(String questionJSON)
			throws JSONException {

		JSONObject jsonParser = new JSONObject(questionJSON);
		long id = jsonParser.getLong("id");

		String question = jsonParser.getString("question");
		JSONArray answersJSON = jsonParser.getJSONArray("answers");
		ArrayList<String> answers = getJSONArrayToStringArray(answersJSON);

		int solutionIndex = jsonParser.getInt("solutionIndex");
		JSONArray tagsJSON = jsonParser.getJSONArray("tags");
		ArrayList<String> tags = getJSONArrayToStringArray(tagsJSON);
		String owner = jsonParser.getString("owner");

		return new Question(id, question, answers, solutionIndex, tags, owner);
	}

	public JSONObject createJSONFromQuestion() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("id", id);
			jsonObject.put("question", questionContent);
			jsonObject.put("answers", answers);
			jsonObject.put("solutionIndex", solutionIndex);
			jsonObject.put("tags", tags);
			jsonObject.put("owner", owner);
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
