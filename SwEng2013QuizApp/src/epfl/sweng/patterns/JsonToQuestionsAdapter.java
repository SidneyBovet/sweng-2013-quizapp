package epfl.sweng.patterns;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.backend.QuizQuery;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * This class has to be used due to adapt the incompatible interface used in
 * INetworkCommunication interface for the client (in our case, the GUI).
 * 
 * Note: This is not really an adapter, since we do not store the adaptee here.
 * It is indeed a singleton.
 */
public final class JsonToQuestionsAdapter {
	
	private JsonToQuestionsAdapter() {
	}

	/**
	 * Used by the GUI to get a list a questions form the server.
	 * 
	 * @param query
	 *            query to pass to the proxy.
	 * @return A list of questions.
	 */
	public static List<QuizQuestion> retrieveQuizQuestions(QuizQuery query) {
		
		JSONObject jsonResponse = QuestionsProxy.getInstance()
				.retrieveQuizQuestions(query);
		List<QuizQuestion> questions = new ArrayList<QuizQuestion>();
		try {
			questions = fillQuizQuestionListFromQuery(jsonResponse);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		// TODO parse JSON object, get questions AND find a way
		// to store the next token that will be parsed (would be a good idea
		// to store it here instead of the proxy!)
		//XXX no need to store the next field?

		return questions;
	}
	
	//XXX find another way to test it without static
	public static List<QuizQuestion> fillQuizQuestionListFromQuery(JSONObject jsonResponse) throws JSONException{
		JSONArray array = null;
		List<QuizQuestion> questions = new ArrayList<QuizQuestion>();
		array = jsonResponse.getJSONArray("questions");
		if(array != null && array.opt(0) != null){
			for (int i = 0; i < array.length(); i++) {
				QuizQuestion question = new QuizQuestion(array.opt(i).toString());
				//TODO add a field in QuizQuery to store the expected tags? Joanna
				//if(question.getTags().contains(query.getTag()))
				questions.add(question);
				//XXX bad recursive idea?
			}
		}
		return questions;
	}
	
	
	private List<QuizQuestion> nextQuery(JSONObject jsonResponse) throws JSONException{
		List<QuizQuestion> questions = new ArrayList<QuizQuestion>();
		while(jsonResponse.getJSONArray("next") != null){
			JSONObject jsonResponseNext = QuestionsProxy.getInstance()
					.retrieveQuizQuestions(new QuizQuery(jsonResponse));
			questions.addAll(fillQuizQuestionListFromQuery(jsonResponseNext));
			jsonResponse = jsonResponseNext;
		}
		return questions;
	}
}	 
