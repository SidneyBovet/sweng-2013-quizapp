package epfl.sweng.test.minimalmock;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;

import android.util.Log;


/** The SwEng HTTP Client */
public class MockHttpClient extends DefaultHttpClient {

    /** Prepared response */
    private class CannedResponse {
        private final Pattern pattern;
        private final int statusCode;
        private final String responseBody;
        private final String contentType;

        public CannedResponse(Pattern pattern, int statusCode,
        	String responseBody, String contentType) {
            this.pattern = pattern;
            this.statusCode = statusCode;
            this.responseBody = responseBody;
            this.contentType = contentType;
        }
    }
    private final Set<CannedResponse> responsesToUseOnlyOnce =
    		new HashSet<CannedResponse>();
    private final List<CannedResponse> responses = new ArrayList<CannedResponse>();
    /* note: those are real HTTP response but we're highly unlikely to want our
     * mock client to return these, therefore using them as internal error codes
     */
    public static final int IOEXCEPTION_ERROR_CODE = 402;
	public static final int CLIENTPROTOCOLEXCEPTION_ERROR_CODE = 414;
	public static final int FORBIDDEN_ERROR_CODE = 400;
    
    public void pushCannedResponse(String requestRegex, int status,
    	String responseBody, String contentType) {
        responses.add(
        	0,
        	new CannedResponse(Pattern.compile(requestRegex),
        		status,
        		responseBody,
        		contentType)
        );
        Log.i("MOCK HTTP CLIENT", "Request "+ requestRegex + " added.");
    }
    public void pushCannedResponse(String requestRegex, int status,
        	String responseBody, String contentType, boolean onlyOnce) {
	    	CannedResponse cannedResponse = 
	    			new CannedResponse(Pattern.compile(requestRegex),
	    					status,
	    					responseBody,
	    					contentType);
            responses.add(0,cannedResponse);
            if (onlyOnce) {
				responsesToUseOnlyOnce.add(cannedResponse);
			}
            Log.i("MOCK HTTP CLIENT", "Request "+ requestRegex + " added.");
    }

    public void popCannedResponse() {
        if (responses.isEmpty()) {
            throw new IllegalStateException("Canned response stack is empty!");
        }
        responses.remove(0);
    }

    @Override
    protected RequestDirector createClientRequestDirector(
            final HttpRequestExecutor requestExec,
            final ClientConnectionManager conman,
            final ConnectionReuseStrategy reustrat,
            final ConnectionKeepAliveStrategy kastrat,
            final HttpRoutePlanner rouplan,
            final HttpProcessor httpProcessor,
            final HttpRequestRetryHandler retryHandler,
            final RedirectHandler redirectHandler,
            final AuthenticationHandler targetAuthHandler,
            final AuthenticationHandler proxyAuthHandler,
            final UserTokenHandler stateHandler,
            final HttpParams params) {
        return new MockRequestDirector(this);
    }

    public HttpResponse processRequest(HttpRequest request) {
        for (CannedResponse cr : responses) {
            if (cr.pattern.matcher(request.getRequestLine().toString()).find()) {
                Log.v("HTTP", "Mocking request since it matches pattern " + cr.pattern);
                Log.v("HTTP", "Response body: " + cr.responseBody);
                return new MockHttpResponse(cr.statusCode, cr.responseBody, cr.contentType);
            }
        }

        return null;
    }

	public boolean responsesIsEmpty() {
		return responses.isEmpty();
	}
	public void usingResponse(HttpResponse response) {
		if (responsesToUseOnlyOnce.contains(response)) {
			responses.remove(response);
			responsesToUseOnlyOnce.remove(responses);
		}
	}
}

/**
 * A request director which does nothing else than passing the request back to
 * the MockHttpClient.
 */
class MockRequestDirector implements RequestDirector {

	private MockHttpClient httpClient;

    public MockRequestDirector(MockHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request,
            HttpContext context) throws IOException, ProtocolException {
        Log.v("HTTP", request.getRequestLine().toString());
		Log.d("HTTP HEADER", Arrays.toString(request.getAllHeaders()));
        
        HttpResponse response = httpClient.processRequest(request);
        if (response == null) {
            throw new AssertionError("Request \"" + request.getRequestLine().toString()
                    + "\" did not match any known pattern");
        } else {
        	httpClient.usingResponse(response);
        }
        switch (response.getStatusLine().getStatusCode()) {
			case MockHttpClient.IOEXCEPTION_ERROR_CODE:
				Log.d("HTTP ERR", "Throwing IOE");
				throw new IOException("Bam!");
			case MockHttpClient.CLIENTPROTOCOLEXCEPTION_ERROR_CODE:
				Log.d("HTTP ERR", "Throwing CPE");
				throw new ClientProtocolException("Take this, code!");
			default:
		        Log.v("HTTP", response.getStatusLine().toString());
		        return response;
        }
    }
}

/** The HTTP Response returned by a MockHttpServer */
class MockHttpResponse extends BasicHttpResponse {
    public MockHttpResponse(int statusCode, String responseBody, String contentType) {
        super(new ProtocolVersion("HTTP", 1, 1),
                statusCode,
                EnglishReasonPhraseCatalog.INSTANCE.getReason(
                        statusCode, Locale.getDefault()));

        if (responseBody != null) {
            try {
                StringEntity responseBodyEntity = new StringEntity(responseBody);
                if (contentType != null) {
                    responseBodyEntity.setContentType(contentType);
                }
                this.setEntity(responseBodyEntity);
            } catch (UnsupportedEncodingException e) {
                // Nothing, really...
            }
        }
    }
}
