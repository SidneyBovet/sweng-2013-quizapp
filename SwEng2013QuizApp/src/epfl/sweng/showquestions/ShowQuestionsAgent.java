package epfl.sweng.showquestions;

import java.util.ArrayDeque;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.backend.QuizQuery;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.quizquestions.QuizQuestion;

public class ShowQuestionsAgent {

	private QuizQuery mQuizQuery;
	private Queue<QuizQuestion> mQuestionQueue;
	
	public ShowQuestionsAgent(QuizQuery query) {
		this.mQuizQuery = query;
		this.mQuestionQueue = new ArrayDeque<QuizQuestion>();
	}
	
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
						for (int i = 0; i < array.length() && array.opt(i) != null; i++) {
							QuizQuestion question = new QuizQuestion(array.opt(i)
									.toString());
							// TODO add a field in QuizQuery to store the expected tags?
							// Joanna
							// if(question.getTags().contains(query.getTag()))
							fetchedQuestions.add(question);	//XXX couldn't call push. Aym
						}
					}
					
					if (jsonResponse.getBoolean("next")) {
						from = jsonResponse.getString("next");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
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
