package epfl.sweng.showquestions;

import java.util.ArrayDeque;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import epfl.sweng.backend.Converter;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * A {@code ShowQuestionsAgent} is responsible of the communication between
 * the {@link ShowQuestionActivity} and the SwEng server.
 * 
 * @author Melody Lucid
 *
 */
public class ShowQuestionsAgent {

	private QuizQuery mQuizQuery;
	private Queue<QuizQuestion> mQuestionQueue;
	
	/**
	 * Creates a {@code ShowQuestionsAgent} that can ask a specific query to the
	 * server.
	 * <p>
	 * If the query is set to {@code null}, the questions retrieved will be
	 * random.
	 * 
	 * @param query The specific query.
	 */
	
	public ShowQuestionsAgent(QuizQuery query) {
		this.mQuizQuery = query;
		this.mQuestionQueue = new ArrayDeque<QuizQuestion>();
	}
	
	/**
	 * Returns the next question to display that matches the query.
	 * <p>
	 * If there's no more question to display, returns a random question.
	 * 
	 * @return The next question to display, or a random question.
	 */
	
	public QuizQuestion getNextQuestion() {
		if (hasNext()) {
			return mQuestionQueue.poll();
		} else if (mQuizQuery == null) {
			return QuestionsProxy.getInstance().retrieveRandomQuizQuestion();
		} else {
			fetchMoreQuestions();
			return getNextQuestion();
		}
	}
	
	/**
	 * Returns {@code true} if there is more questions that match the query.
	 * 
	 * @return {@code true} if there is more questions that match the query.
	 */
	
	public boolean hasNext() {
		return mQuestionQueue.size() != 0;
	}
	
	private void fetchMoreQuestions() {
		if (mQuizQuery != null) {
			Queue<QuizQuestion> fetchedQuestions = new ArrayDeque<QuizQuestion>();
			String from = null;
			try {
				JSONObject jsonResponse = QuestionsProxy.getInstance()
						.retrieveQuizQuestions(mQuizQuery);
				if (jsonResponse != null) {
					
					JSONArray array = jsonResponse.getJSONArray("questions");
					if (array != null) {
						// TODO add a field in QuizQuery to store the expected tags?
						// Joanna
						// if(question.getTags().contains(query.getTag()))
						fetchedQuestions = new ArrayDeque<QuizQuestion>(
								Converter.jsonArrayToQuizQuestionList(array));
					}
					
					if (jsonResponse.getBoolean("next")) {
						from = jsonResponse.getString("next");
					}
				}
			} catch (JSONException e) {
				Log.e(this.getClass().getName(), "fetchMoreQuestions(): wrong " +
						"structure of JSON response.", e);
			}
			
			mQuestionQueue = fetchedQuestions;
			if (from != null) {
				mQuizQuery = new QuizQuery(mQuizQuery.getQuery(), from);
			} else {
				mQuizQuery = null;
			}
		}
	}
}
