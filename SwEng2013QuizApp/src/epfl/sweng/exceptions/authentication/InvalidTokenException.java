package epfl.sweng.exceptions.authentication;

/**
 * This exception is thrown when there has been a problem with
 * the token validation process between the client and the Sweng server.
 * @author born4new
 *
 */
@SuppressWarnings("serial")
public class InvalidTokenException extends Exception {
	public InvalidTokenException(String msg) {
		super(msg);
	}
}
