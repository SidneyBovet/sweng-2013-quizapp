package epfl.sweng.exceptions;

/**
 * This exception arises when we had an unknown problem related to the submit of
 * a question.
 * 
 * @author born4new
 *
 */
@SuppressWarnings("serial")
public class ServerSubmitFailedException extends Exception {
	
	public ServerSubmitFailedException(String message) {
		super(message);
	}
}
