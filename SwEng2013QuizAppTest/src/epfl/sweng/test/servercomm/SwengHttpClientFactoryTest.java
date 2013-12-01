package epfl.sweng.test.servercomm;

import org.apache.http.impl.client.AbstractHttpClient;

import junit.framework.TestCase;
import epfl.sweng.servercomm.SwengHttpClientFactory;

public class SwengHttpClientFactoryTest extends TestCase {

	public void testCreation() {
		AbstractHttpClient client = SwengHttpClientFactory.getInstance();
		
		assertNotNull("Client retrieved should not be null", client);
	}
}
