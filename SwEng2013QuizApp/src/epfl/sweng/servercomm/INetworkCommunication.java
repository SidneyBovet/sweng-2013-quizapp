package epfl.sweng.servercomm;

import org.json.JSONObject;

import epfl.sweng.backend.QuizQuery;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * How to communicate with the server.
 * @author born4new
 *
 */
public interface INetworkCommunication {
	
	/**
	 * Sends a quiz question to the server. 
	 * @param question Question to be sent.
	 * @return The response status from the server.
	 */
	int sendQuizQuestion(QuizQuestion question);
	
	/**
	 * Retrieve a question from the server.
	 * @return Question retrieved.
	 */
	QuizQuestion retrieveRandomQuizQuestion();
	
	/**
	 * Retrieve a question from the server using a filtering query.
	 * @return JSON sent by the server.
	 */
	JSONObject retrieveQuizQuestions(QuizQuery query);
}
