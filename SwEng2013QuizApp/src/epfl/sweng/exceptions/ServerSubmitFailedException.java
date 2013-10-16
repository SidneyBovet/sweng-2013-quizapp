package epfl.sweng.exceptions;

/**
 * This exception arises when we had an unknown problem
 * related to the submit of a question.
 * @author born4new
 *
 */
public class ServerSubmitFailedException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4359358644227399844L;

	public ServerSubmitFailedException(String message) {
		super(message);
	}
}
