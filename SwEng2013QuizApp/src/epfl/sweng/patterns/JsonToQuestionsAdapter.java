package epfl.sweng.patterns;

import java.util.List;

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

		// TODO parse JSON object, get questions AND find a way
		// to store the next token that will be parsed (would be a good idea
		// to store it here instead of the proxy!)

		return null;
	}
}
