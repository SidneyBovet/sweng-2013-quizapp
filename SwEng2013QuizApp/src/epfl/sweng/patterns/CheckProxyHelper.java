package epfl.sweng.patterns;

import epfl.sweng.servercomm.NetworkCommunication;

/**
 * Used for sweng Jenkins' testing.
 * @author born4new
 *
 */
public class CheckProxyHelper implements ICheckProxyHelper {

	@Override
	public Class<?> getServerCommunicationClass() {
		return NetworkCommunication.class;
	}

	@Override
	public Class<QuestionsProxy> getProxyClass() {
		return QuestionsProxy.class;
	}

}
