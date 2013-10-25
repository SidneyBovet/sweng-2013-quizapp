package epfl.sweng.patterns;

public class CheckProxyHelper implements ICheckProxyHelper {

	@Override
	public Class<?> getServerCommunicationClass() {
		return QuestionsProxy.class;
	}

	@Override
	public Class<QuestionsProxy> getProxyClass() {
		return QuestionsProxy.class;
	}

}
