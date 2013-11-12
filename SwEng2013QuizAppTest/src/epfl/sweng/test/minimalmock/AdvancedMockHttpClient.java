package epfl.sweng.test.minimalmock;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.apache.http.client.methods.HttpPost;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


/** An advanced mock HTTP Client doing more than the {@link MockHttpClient} */
public class AdvancedMockHttpClient extends DefaultHttpClient {

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

		public int getStatus() {
			return this.statusCode;
		}
    }
    private final Set<CannedResponse> responsesToUseOnlyOnce =
    		new HashSet<CannedResponse>();
    private final List<CannedResponse> responses = new ArrayList<CannedResponse>();
	private String mLasSubmittedQuestionStatement = null;
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
    
    /**
     * 
     * @param requestRegex
     * 		The regular expression used for matching a request to this response
     * @param status
     * 		The status that will be answered with
     * @param responseBody
     * 		The body of the http response
     * @param contentType
     * 		The type of the content of the response
     * @param onlyOnce
     * 		Allows to specify whether this canned response should be used once
     * 		and then be removed (single-use response) 
     */
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
        return new AdvancedMockRequestDirector(this);
    }

    public HttpResponse processRequest(HttpRequest request) {
    	
    	storePostedQuestionIfApplicable(request);
    	
        for (CannedResponse cr : responses) {
            if (cr.pattern.matcher(request.getRequestLine().toString()).find()) {
                Log.v("HTTP", "Mocking request since it matches pattern " + cr.pattern);
                Log.v("HTTP", "Response body: " + cr.responseBody +
                		", response status=" + cr.getStatus());
                
                usingResponse(cr);
                
                return new AdvancedMockHttpResponse(cr.statusCode, cr.responseBody, cr.contentType);
            }
        }

        return null;
    }

	public boolean responsesIsEmpty() {
		return responses.isEmpty();
	}
	
	public void usingResponse(CannedResponse response) {
		if (responsesToUseOnlyOnce.contains(response)) {
			responses.remove(response);
			responsesToUseOnlyOnce.remove(responses);
		}
	}

	public String getLastSubmittedQuestionStatement() {
		return mLasSubmittedQuestionStatement;
	}

	public Object getResponsesListSize() {
		return responses.size();
	}
	
	/**
	 * Analyzes the HttpRequest and extracts the question's statement if the
	 * request is a POST sending a QuizQuestion to the sweng server. 
	 * 
	 * @param request The request to analyze
	 */
	private void storePostedQuestionIfApplicable(HttpRequest request) {
		// Yes, I did slap me in the face before writing that... Sidney
		if (request instanceof HttpPost) {
				String content = "--not yet set--";
				HttpPost post = (HttpPost) request;
				try {
					InputStream in = post.getEntity().getContent();
					
					content = convertStreamToString(in, "UTF-8");
					
					JSONObject json = new JSONObject(content);
					
					mLasSubmittedQuestionStatement = json.getString("question");
				} catch (IllegalStateException e) {
					Log.e(this.getClass().getName(), "ISE occured during " +
							"storing a question posted.", e);
				} catch (IOException e) {
					Log.e(this.getClass().getName(), "IOE occured during " +
							"storing a question posted.", e);
				} catch (JSONException e) {
					Log.e(this.getClass().getName(), "JSONE occured during " +
							"storing a question posted.", e);
				}
		}
	}

	private static String convertStreamToString( InputStream is, String ecoding ) throws IOException
	{
	    StringBuilder sb = new StringBuilder( Math.max( 16, is.available() ) );
	    char[] tmp = new char[ 4096 ];

	    try {
	       InputStreamReader reader = new InputStreamReader( is, ecoding );
	       for( int cnt; ( cnt = reader.read( tmp ) ) > 0; )
	            sb.append( tmp, 0, cnt );
	    } finally {
	        is.close();
	    }
	    return sb.toString();
	}
}

/**
 * A request director which does nothing else than passing the request back to
 * the MockHttpClient.
 */
class AdvancedMockRequestDirector implements RequestDirector {

	private AdvancedMockHttpClient httpClient;

    public AdvancedMockRequestDirector(AdvancedMockHttpClient httpClient) {
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
        }
        switch (response.getStatusLine().getStatusCode()) {
			case AdvancedMockHttpClient.IOEXCEPTION_ERROR_CODE:
				Log.d("HTTP ERR", "Throwing IOE");
				throw new IOException("Bam!");
			case AdvancedMockHttpClient.CLIENTPROTOCOLEXCEPTION_ERROR_CODE:
				Log.d("HTTP ERR", "Throwing CPE");
				throw new ClientProtocolException("Take this, code!");
			default:
		        Log.v("HTTP", response.getStatusLine().toString());
		        return response;
        }
    }
}

/** The HTTP Response returned by a MockHttpServer */
class AdvancedMockHttpResponse extends BasicHttpResponse {
    public AdvancedMockHttpResponse(int statusCode, String responseBody, String contentType) {
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
