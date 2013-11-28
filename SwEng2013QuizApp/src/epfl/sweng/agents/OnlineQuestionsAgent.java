package epfl.sweng.agents;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import epfl.sweng.backend.Converter;
import epfl.sweng.backend.QuizQuery;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.INetworkCommunication;

/**
 * A {@code ShowQuestionsAgent} is responsible of the communication between
 * the {@link ShowQuestionActivity} and the SwEng server.
 * 
 * @author Melody Lucid
 *
 */
public class OnlineQuestionsAgent extends QuestionAgent {

	private QuizQuery mQuizQuery;
	private Queue<QuizQuestion> mQuestionQueue;
	private INetworkCommunication mNetworkComm;
	
	/**
	 * Creates a {@code ShowQuestionsAgent} that can ask a specific query to the
	 * server.
	 * <p>
	 * If the query is set to {@code null}, the questions retrieved will be
	 * random.
	 * 
	 * @param query The specific query.
	 */
	
	public OnlineQuestionsAgent(QuizQuery query) {
		super(query);
		if (null == query) {
			this.mQuizQuery = new QuizQuery();
		} else {
			this.mQuizQuery = query;
		}
		this.mQuestionQueue = new ArrayDeque<QuizQuestion>();
		this.mNetworkComm = QuestionsProxy.getInstance();
	}
	
	/**
	 * Returns the next question to display that matches the query.
	 * <p>
	 * Returns a random question if there's no query to post, and
	 * <code>null</code> if there's no more question to display.
	 * 
	 * @return The next question to display, or a random question.
	 */
	
	@Override
	public QuizQuestion getNextQuestion() {
		if (hasNext()) {
			// TODO cache the question polled
			return mQuestionQueue.poll();
		} else if (mQuizQuery.isRandom()) {
			return mNetworkComm.retrieveRandomQuizQuestion();
		} else if (mQuizQuery.getFrom() != null) {
			fetchMoreQuestions();
			return getNextQuestion();
		} else {
			return null;
		}
	}
	
	@Override
	public void close() { }

	@Override
	public boolean isClosed() {
		return false;
	}
	
	/**
	 * Sets a new instance of {@link INetworkCommunication}, which describes a
	 * new way to communicate with the server.
	 * 
	 * @param networkComm new instance of {@link INetworkCommunication}.
	 */
	
	public void setNetworkCommunication(INetworkCommunication networkComm) {
		this.mNetworkComm = networkComm;
	}

	/**
	 * Returns {@code true} if there is more questions that match the query.
	 * 
	 * @return {@code true} if there is more questions that match the query.
	 */
	
	private boolean hasNext() {
		return mQuestionQueue.size() != 0;
	}
	
	private void fetchMoreQuestions() {
		if (mQuizQuery != null) {
			Queue<QuizQuestion> fetchedQuestions = new ArrayDeque<QuizQuestion>();
			String next = null;
			try {
				JSONObject jsonResponse = mNetworkComm
						.retrieveQuizQuestions(mQuizQuery);
				if (jsonResponse != null) {
					
					JSONArray array = jsonResponse.getJSONArray("questions");
					if (array != null) {
						List<QuizQuestion> list = Converter.jsonArrayToQuizQuestionList(array);
						if (list != null) {
							fetchedQuestions = new ArrayDeque<QuizQuestion>(list);
						}
					}
					
					next = jsonResponse.optString("next", null);
				}
			} catch (JSONException e) {
				Log.e(this.getClass().getName(), "fetchMoreQuestions(): wrong " +
						"structure of JSON response.", e);
			}
			
			mQuestionQueue = fetchedQuestions;
			mQuizQuery = new QuizQuery(mQuizQuery.getQuery(), next);
		}
	}
}
