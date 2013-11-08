package epfl.sweng.servercomm;

import epfl.sweng.quizquestions.QuizQuestion;

/**
 * How to communicate with the server.
 * @author born4new
 *
 */
public interface INetworkCommunication {
	
	/**
	 * Sends a quizz question to the server. 
	 * @param question Question to be sent.
	 * @return The response status from the server.
	 */
	int sendQuizQuestion(QuizQuestion question);
	
	/**
	 * Retrieve a question from the server.
	 * @return Question retrieved.
	 */
	QuizQuestion retrieveQuizQuestion();
}
