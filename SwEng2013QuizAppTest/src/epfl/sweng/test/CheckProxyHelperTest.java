package epfl.sweng.test;

import junit.framework.TestCase;
import epfl.sweng.patterns.CheckProxyHelper;
import epfl.sweng.patterns.QuestionsProxy;
import epfl.sweng.servercomm.NetworkCommunication;

public class CheckProxyHelperTest extends TestCase{
	public void testTheOnlyOne(){
		CheckProxyHelper checkproxy = new CheckProxyHelper();
		assertEquals(NetworkCommunication.class, 
				checkproxy.getServerCommunicationClass());
		assertEquals(QuestionsProxy.class, 
				checkproxy.getProxyClass());
	}
}
