package epfl.sweng.exceptions.authentication;

/**
 * This exception is thrown when there has been a problem with the initial
 * communication between the client and the Tequila server to get the first
 * token.
 * 
 * @author born4new
 *
 */
@SuppressWarnings("serial")
public class TequilaNoTokenException extends Exception {
	
	public TequilaNoTokenException(String msg) {
		super(msg);
	}
}
