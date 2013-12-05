package epfl.sweng.showquestions;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import epfl.sweng.backend.Converter;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.comm.QuestionCommunication;
import epfl.sweng.comm.QuestionProxy;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * A {@code ShowQuestionsAgent} is responsible of the communication between the
 * {@link ShowQuestionActivity} and the SwEng server.
 * 
 * @author Melody Lucid
 * 
 */

public class ShowQuestionsAgent {

	private QuizQuery mQuizQuery;
	private Queue<QuizQuestion> mQuestionQueue;
	private QuestionCommunication mQuestionComm;

	/**
	 * Creates a {@code ShowQuestionsAgent} that can ask a specific query to the
	 * server.
	 * <p>
	 * If the query is set to {@code null}, the questions retrieved will be
	 * random.
	 * 
	 * @param query
	 *            The specific query.
	 */

	public ShowQuestionsAgent(QuizQuery query) {
		if (null == query) {
			this.mQuizQuery = new QuizQuery(null, null);
		} else {
			this.mQuizQuery = query;
		}
		this.mQuestionQueue = new ArrayDeque<QuizQuestion>();
		this.mQuestionComm = QuestionProxy.getInstance();
	}

	/**
	 * Returns the next question to display that matches the query.
	 * <p>
	 * Returns a random question if there's no query to post, and
	 * <code>null</code> if there's no more question to display.
	 * 
	 * @return The next question to display, or a random question.
	 */

	public QuizQuestion getNextQuestion() {
		if (mQuizQuery.isRandom()) {
			JSONObject randomJSON = mQuestionComm.retrieveRandomQuizQuestion();
			try {
				if (randomJSON != null) {
					return new QuizQuestion(randomJSON.toString());
				} else {
					return null;
				}
			} catch (JSONException e) {
				Log.e(this.getClass().getName(), "getNextQuestion(): "
						+ "QuizQuestion JSON input was incorrect", e);
				return null;
			}
		} else if (hasNext()) {
			return mQuestionQueue.poll();
		} else {
			fetchMoreQuestions();
			return getNextQuestion();
		}
	}

	/**
	 * Sets a new instance of {@link INetworkCommunication}, which describes a
	 * new way to communicate with the server.
	 * 
	 * @param networkComm
	 *            new instance of {@link INetworkCommunication}.
	 */

	public void setNetworkCommunication(QuestionCommunication questionComm) {
		this.mQuestionComm = questionComm;
	}

	/**
	 * Returns {@code true} if there is more questions that match the query.
	 * 
	 * @return {@code true} if there is more questions that match the query.
	 */

	private boolean hasNext() {
		return mQuestionQueue.size() != 0 || mQuizQuery.getFrom() == null;
	}

	private void fetchMoreQuestions() {
		if (mQuizQuery != null) {
			Queue<QuizQuestion> fetchedQuestions = new ArrayDeque<QuizQuestion>();
			String next = null;
			try {
				JSONObject jsonResponse = mQuestionComm
						.retrieveQuizQuestion(mQuizQuery);
				if (jsonResponse != null) {

					JSONArray array = jsonResponse.getJSONArray("questions");
					if (array != null) {
						List<QuizQuestion> list = Converter
								.jsonArrayToQuizQuestionList(array);
						if (list != null) {
							fetchedQuestions = new ArrayDeque<QuizQuestion>(
									list);
						}
					}

					next = jsonResponse.optString("next", null);
				}
			} catch (JSONException e) {
				Log.e(this.getClass().getName(), "fetchMoreQuestions(): wrong "
						+ "structure of JSON response.", e);
			}

			mQuestionQueue = fetchedQuestions;
			mQuizQuery = new QuizQuery(mQuizQuery.getQuery(), next);
		}
	}
}