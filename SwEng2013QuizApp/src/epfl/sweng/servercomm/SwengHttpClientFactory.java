package epfl.sweng.servercomm;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectHandler;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

/**
 * The <code>SwengHttpClientFactory</code> class is an HTTP Client factory. The
 * client is unique, it is returned by the {@link #getInstance()} method.
 * <p>
 * It also allows to inject custom HTTP Clients for testing purpose, by the 
 * method {@link #setInstance(AbstractHttpClient)}.
 * 
 * @author ???
 * 
 */
public class SwengHttpClientFactory {
	
	private static AbstractHttpClient sHttpClient;
	private static final int HTTP_PORT = 80;
	private final static int HTTPS_PORT = 443;
	
	/**
	 * Returns the unique instance of the {@link AbstractHttpClient}. The
	 * instance will be created if it doesn't exist.
	 * 
	 * @return The HTTP Client instance.
	 */
	
	public static synchronized AbstractHttpClient getInstance() {
		if (sHttpClient == null) {
			sHttpClient = create();
		}

		return sHttpClient;
	}
	
	/**
	 * Sets a new value to the {@link AbstractHttpClient}. This has been
	 * implemented for testing purpose.
	 * 
	 * @param A new HTTP Client instance.
	 */
	
	public static synchronized void setInstance(AbstractHttpClient instance) {
		sHttpClient = instance;
	}
	
	final static private RedirectHandler REDIRECT_NO_FOLLOW = new RedirectHandler() {
		
		@Override
		public boolean isRedirectRequested(HttpResponse response,
				HttpContext context) {
			return false;
		}
		
		@Override
		public URI getLocationURI(HttpResponse response, HttpContext context)
			throws org.apache.http.ProtocolException {
			return null;
		}
	};
	
	final static private CookieStore COOKIE_MONSTER = new CookieStore() {
		@Override
		public boolean clearExpired(Date date) {
			return true;
		}
		
		@Override
		public List<Cookie> getCookies() {
			return new ArrayList<Cookie>();
		}
		
		@Override
		public void addCookie(Cookie cookie) {
			// Nothing to do here
		}
		
		@Override
		public void clear() {
			// Nothing to do here
		}
	};
	
	final static private HttpRequestInterceptor LOGGING_REQUEST_INTERCEPTOR = new HttpRequestInterceptor() {
		
		@Override
		public void process(HttpRequest request, HttpContext context) {
			Log.d("HTTP REQUEST", request.getRequestLine().toString());
		}
	};
	
	final private static HttpResponseInterceptor LOGGING_RESPONSE_INTERCEPTOR = new HttpResponseInterceptor() {
		
		@Override
		public void process(HttpResponse response, HttpContext context) {
			Log.d("HTTP RESPONSE", response.getStatusLine().toString());
		}
	};
	
	/**
	 * Creates an instance of {@link DefaultHttpClient}, with predefined default
	 * values.
	 * 
	 * @return A new HTTP Client instance.
	 */
	
	static private AbstractHttpClient create() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), HTTP_PORT));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), HTTPS_PORT));
		
		HttpParams params = new BasicHttpParams();
		
		ThreadSafeClientConnManager connManager =
				new ThreadSafeClientConnManager(params, schemeRegistry);
		AbstractHttpClient result = new DefaultHttpClient(connManager, params);
		
		result.setRedirectHandler(REDIRECT_NO_FOLLOW);
		result.setCookieStore(COOKIE_MONSTER);
		result.addRequestInterceptor(LOGGING_REQUEST_INTERCEPTOR);
		result.addResponseInterceptor(LOGGING_RESPONSE_INTERCEPTOR);
		
		return result;
	}
}
