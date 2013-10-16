package epfl.sweng.test;

import junit.framework.TestCase;
import epfl.sweng.exceptions.ServerSubmitFailedException;
import epfl.sweng.exceptions.authentication.InvalidTokenException;
import epfl.sweng.exceptions.authentication.NoSessionIDException;
import epfl.sweng.exceptions.authentication.TequilaNoTokenException;

/**
 * Test case that covers our package epfl.sweng.exceptions.
 * 
 * @author Melody Lucid
 *
 */
public class ExceptionTest extends TestCase {

	/**
	 * Tests if we can instantiate our custom Exceptions. Really because we have
	 * to...
	 */
	
	public void testExceptionCreation() {
		new ServerSubmitFailedException("");
		new InvalidTokenException("");
		new NoSessionIDException("");
		new TequilaNoTokenException("");
		assertTrue(true);
	}
}
