package epfl.sweng.agents;

import epfl.sweng.backend.QuizQuery;
import epfl.sweng.quizquestions.QuizQuestion;

/**
 * Superclass for all question agents<br/>
 * These are objects mandatory for providing questions according to a stream
 * 
 * @author Sidney
 */
public abstract class QuestionAgent {
	private QuizQuery mQuery;
	
	public QuestionAgent(QuizQuery query) {
		mQuery = query;
	}
	
	public abstract QuizQuestion getNextQuestion();

	public abstract void close();

	public QuizQuery getQuery() {
		return mQuery;
	}

	public abstract boolean isClosed();
}
