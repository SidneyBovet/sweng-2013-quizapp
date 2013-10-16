package epfl.sweng.exceptions;

/**
 * This exception is thrown when there has been a problem while sending an
 * EditedQuiz to the Server. server to get the first token. It informs the User
 * that the quiz has not been send.
 * 
 * @author Merok
 * 
 */
@SuppressWarnings("serial")
public class SendEditedQuestionFailException extends Exception {
	public SendEditedQuestionFailException(String msg) {
		super(msg);
	}
}
