package epfl.sweng.backend;

import epfl.sweng.quizquestions.QuizQuestion;

/**
 * Superclass for all question agents<br/>
 * These are objects mandatory for providing questions according to a stream
 * 
 * @author Sidney
 */
public abstract class QuestionAgent {
	// XXX Sidney better to use an interface huh?
	public abstract QuizQuestion getNextQuestion();

	public abstract void destroy();
}
