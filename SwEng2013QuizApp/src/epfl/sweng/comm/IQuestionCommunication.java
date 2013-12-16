package epfl.sweng.comm;

import org.json.JSONObject;

import epfl.sweng.backend.QuizQuery;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * Interface for the communication service of {@link QuizQuestion}.
 * 
 * @author Melody Lucid
 * 
 */
public interface IQuestionCommunication {

	/**
	 * Sends a {@link QuizQuestion}.
	 * 
	 * @param quizQuestion
	 *            Question to send.
	 * @return HTTP status response code.
	 */
	
	int sendQuizQuestion(QuizQuestion quizQuestion);
	
	/**
	 * Retrieves a {@link QuizQuestion}, according to a specific
	 * {@link QuizQuery}.
	 * 
	 * @param quizQuery
	 *            Query that specifies the Question to retrieve.
	 * @return JSONObject containing an array of questions, with a possible
	 *            <code>next</code> field.
	 */
	
	JSONObject retrieveQuizQuestion(QuizQuery quizQuery);
	
	/**
	 * Retrieves a random {@link QuizQuestion}.
	 * 
	 * @return JSONObject containing a single structure of {@link QuizQuestion}.
	 */
	
	JSONObject retrieveRandomQuizQuestion();
	
	/**
	 * Closes the communication.
	 */
	
	void close();
}
