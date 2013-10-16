package epfl.sweng.exceptions.authentication;

/**
 * This exception is thrown when there has been a problem with
 * the token validation process between the client and the Sweng server.
 * @author born4new
 *
 */
@SuppressWarnings("serial")
public class InvalidatedTokenException extends Exception {
	public InvalidatedTokenException(String msg) {
		super(msg);
	}
}
