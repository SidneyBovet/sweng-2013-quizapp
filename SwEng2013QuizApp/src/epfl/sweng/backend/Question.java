package epfl.sweng.backend;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	public Question(long questionId, String questionStmt,
			JSONArray questionAnswers, int questionSolutionIndex,
			JSONArray questionTags, String questionOwner) {
		this.id = questionId;
		this.questionContent = questionStmt;
		this.answers = questionAnswers;
		this.solutionIndex = questionSolutionIndex;
		this.tags = questionTags;
		this.owner = questionOwner;
	}

	/**
	 * Processes a request in an {@link AsyncTask}.
	 * @return The parsed question.
	 */
	public static Question getRandomQuestion() {
		DownloadJSONFromServer asyncTaskRandomQuestionGetter = new DownloadJSONFromServer();
		asyncTaskRandomQuestionGetter.execute();
		
		Question question = null;
		try {
			question = Question.createQuestionFromJSON(asyncTaskRandomQuestionGetter.get());
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

	/**
	 * Get one JSON object after the other, transform then to string and put
	 * them in an array list of string
	 * 
	 * @return an array list of string containing the JSONObjects
	 */
	public ArrayList<String> getAnswerToStringArray() {
		ArrayList<String> list = new ArrayList<String>();
		if (answers != null) {
			for (int i = 0; i < answers.length(); i++) {
				if (answers.optString(i) != null) {
					list.add(answers.optString(i));
				}
			}
		}
		return list;
	}

	public int getSolutionIndex() {
		return solutionIndex;
	}

	public JSONArray getTags() {
		return tags;
	}

	/**
	 * Returns a single tag
	 * 
	 * @param index
	 * @return The i-th tag of the Question object
	 * @throws JSONException
	 */
	public String getTagToString(int index) throws JSONException {
		return tags.getString(index);
	}

	/**
	 * Returns all the tag together
	 * 
	 * @return all the tag delimited by ","
	 * @throws JSONException
	 */
	public String getTagsToString() throws JSONException {
		String tagsTogether = "tags: ";
		for (int i = 0; i < (tags.length()); i++) {
			tagsTogether += getTagToString(i);
			if(i < tags.length()-1){
			tagsTogether += ", ";
			}
		}
		return tagsTogether;
	}

	public String getOwner() {
		return owner;
	}

	@Override
	public String toString() {
		return "Question [id=" + id + ", questionContent=" + questionContent
				+ ", answers=" + answers.toString() + ", solutionIndex="
				+ solutionIndex + ", tags=" + tags.toString() + ", owner="
				+ owner + "]";
	}
	
}
