package epfl.sweng.patterns;

import epfl.sweng.comm.OnlineCommunication;
import epfl.sweng.comm.QuestionProxy;

/**
 * Used for sweng Jenkins' testing.
 * @author born4new
 *
 */
public class CheckProxyHelper implements ICheckProxyHelper {

	@Override
	public Class<?> getServerCommunicationClass() {
		return OnlineCommunication.class;
	}

	@Override
	public Class<QuestionProxy> getProxyClass() {
		return QuestionProxy.class;
	}

}
