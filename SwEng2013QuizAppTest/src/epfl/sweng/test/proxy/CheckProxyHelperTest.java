package epfl.sweng.test.proxy;

import junit.framework.TestCase;
import epfl.sweng.comm.OnlineCommunication;
import epfl.sweng.comm.QuestionProxy;
import epfl.sweng.patterns.CheckProxyHelper;

public class CheckProxyHelperTest extends TestCase{
	public void testTheOnlyOne(){
		CheckProxyHelper checkproxy = new CheckProxyHelper();
		assertEquals(OnlineCommunication.class, 
				checkproxy.getServerCommunicationClass());
		assertEquals(QuestionProxy.class, 
				checkproxy.getProxyClass());
	}
}
