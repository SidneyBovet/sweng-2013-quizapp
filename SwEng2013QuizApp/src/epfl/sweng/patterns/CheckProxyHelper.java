package epfl.sweng.patterns;

public class CheckProxyHelper implements ICheckProxyHelper {

	//TODO check before sumbmit if still the good class
	@Override
	public Class<?> getServerCommunicationClass() {
		return QuestionsProxy.class;
	}

	@Override
	public Class<QuestionsProxy> getProxyClass() {
		return QuestionsProxy.class;
	}

}
