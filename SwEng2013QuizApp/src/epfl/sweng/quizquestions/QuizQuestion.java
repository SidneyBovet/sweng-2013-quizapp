package epfl.sweng.quizquestions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data structure of a question for the quiz application.
 * 
 * @author born4new
 * 
 */
public class QuizQuestion {

	private long mId;
	private String mQuestionContent;
	private List<String> mAnswers;
	private int mSolutionIndex;
	private Set<String> mTags;
	private String mOwner;
	final static int QUESTION_CONTENT_MAX_SIZE = 500;
	final static int ANSWER_CONTENT_MAX_SIZE = 500;
	final static int ANSWERLIST_MAX_SIZE = 10;
	final static int TAGSLIST_MAX_SIZE = 20;

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

	public QuizQuestion(final String question, final List<String> answers,
			final int solutionIndex, final Set<String> tags, final int id,
			final String owner) {
		this.mId = id;
		this.mQuestionContent = question;
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

	public QuizQuestion(final String question, final List<String> answers,
			final int solutionIndex, final Set<String> tags) {
		this(question, answers, solutionIndex, tags, -1, null);
	}

	/**
	 * Constructor from a string field of a {@link JSONObject}
	 * 
	 * @param jsonInput
	 *            The {@link JSONObject} string field to convert.
	 * @throws JSONException
	 */
	public QuizQuestion(final String jsonInput) throws JSONException {
		JSONObject jsonParser = new JSONObject(jsonInput);
		long id = jsonParser.getLong("id");

		String question = jsonParser.getString("question");
		JSONArray answersJSON = jsonParser.getJSONArray("answers");
		List<String> answers = Converter.jsonArrayToStringArray(answersJSON);

		int solutionIndex = jsonParser.getInt("solutionIndex");
		JSONArray tagsJSON = jsonParser.getJSONArray("tags");
		Set<String> tags = Converter.jsonArrayToStringSet(tagsJSON);
		String owner = jsonParser.getString("owner");

		this.mId = id;
		this.mQuestionContent = question;
		this.mAnswers = answers;
		this.mSolutionIndex = solutionIndex;
		this.mTags = tags;
		this.mOwner = owner;
	}

	/**
	 * Creates an instance of <code>QuizQuestion</code> from a list of elements,
	 * containing the question text body, the tags, the solution index, and the
	 * answers, then returns it.
	 * 
	 * @param listElm
	 *            A list of the question data fields.
	 * @return A <code>QuizQuestion</code> instance created from a list of
	 *         fields.
	 */

	public static QuizQuestion createQuestionFromList(List<String> listElm) {
		String questionText = listElm.remove(0);
		String tagsInOneLine = listElm.remove(listElm.size() - 1);
		int solutionIndex = Integer
				.parseInt(listElm.remove(listElm.size() - 1));

		Pattern pattern = Pattern.compile("(\\w+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(tagsInOneLine);

		Set<String> tagSet = new TreeSet<String>();
		while (matcher.find()) {
			tagSet.add(matcher.group(1));
		}

		List<String> answerList = new ArrayList<String>();
		for (String answer : listElm) {
			answerList.add(answer);
		}

		return new QuizQuestion(questionText, answerList, solutionIndex, tagSet);
	}

	/**
	 * Returns all the tags in a single <code>String</code>, delimited by a
	 * comma.
	 * 
	 * @return A printable representation of all the tags.
	 * @throws JSONException
	 */

	public String getTagsToString() {
		Iterator<String> tagsIterator = mTags.iterator();
		String tagsTogether = "Tags: ";
		while (tagsIterator.hasNext()) {
			tagsTogether += tagsIterator.next();
			if (tagsIterator.hasNext()) {
				tagsTogether += ", ";
			}
		}
		return tagsTogether;
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
			JSONArray jsonArrayAnswers = new JSONArray(mAnswers);
			jsonObject.put("answers", jsonArrayAnswers);
			jsonObject.put("solutionIndex", mSolutionIndex);
			JSONArray jsonArrayTags = new JSONArray(mTags);
			jsonObject.put("tags", jsonArrayTags);
			jsonObject.put("owner", mOwner);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
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

	/*
	 * ***************************************************
	 * ********************* Audit ***********************
	 * ***************************************************
	 */
	// TODO Add the audit function where needed.

	/**
	 * Audit method that verifies if all rep-invariants are respected.
	 * 
	 * @return a number that represent how many rep-invariants are violated.
	 */
	public int auditErrors() {
		int errorCount = 0;
		if (mQuestionContent.trim().length() == 0
				|| !(0 < mQuestionContent.length() && mQuestionContent.length() <= QUESTION_CONTENT_MAX_SIZE)) {
			++errorCount;
		}

		for (String answer : mAnswers) {
			if (answer.trim().length() == 0
					|| !(0 < answer.length() && answer.length() <= ANSWER_CONTENT_MAX_SIZE)) {
				++errorCount;
			}
		}

		if (!(mAnswers.size() >= 0 && mAnswers.size() <= ANSWERLIST_MAX_SIZE)) {
			++errorCount;
		}

		if (!(mSolutionIndex >= 0 && mSolutionIndex < mAnswers.size())) {
			++errorCount;
		}

		for (String tag : mTags) {
			if (tag.trim().length() == 0
					|| !(0 < tag.length() && tag.length() <= TAGSLIST_MAX_SIZE)) {
				++errorCount;
			}
		}

		return errorCount;
	}

	/*
	 * ***************************************************
	 * **************** Getters & Setters ****************
	 * ***************************************************
	 */

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

	public Set<String> getTags() {
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

}