package epfl.sweng.exceptions.authentication;

/**
 * This exception is thrown when there has been a problem in the 
 * last authentication phase between the client and the Sweng server.
 * @author born4new
 *
 */
@SuppressWarnings("serial")
public class NoSessionIDException extends Exception {
	public NoSessionIDException(String msg) {
		super(msg);
	}
}
