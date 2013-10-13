package epfl.sweng.servercomm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * AsyncTask that performs the networking part of authentication with EPFL's
 * Tequila server. Arguments to passed to its <code>execute()</code> method
 * are:
 * 1. Username
 * 2. Password
 * Any call different from those will throw an IllegalArgumentException.
 * @author Sidney
 *
 */
public class AuthenticationProcess extends AsyncTask<String, Void, String> {
	private static final int HTTP_STATUS_FOUND = 302;
	private ProgressDialog dialog;
	private Context context;
	private String sessionId;
	private final String[] urls = {
		"https://sweng-quiz.appspot.com/login",
		"https://tequila.epfl.ch/cgi-bin/tequila/login",
		"https://sweng-quiz.appspot.com/login"
	};
	
	public AuthenticationProcess(Context ctx) {
		this.context = ctx;
		this.dialog = new ProgressDialog(ctx);
		this.sessionId = "";
	}
	
	@Override
	protected void onPreExecute() {
		dialog.setMessage("Authenticating...");
		dialog.show();
	}
	
	@Override
	protected String doInBackground(String... args) {
		if (args.length != 2) {
			System.err.println("Illegal arguments given to " +
					"AutehnticationProcess, should be (usrname, pwd)");
			throw new IllegalArgumentException("AuthenticationProcess " +
					"recieved " + args.length + "argument(s), should've " +
					"been 2.");
		}
		
		String token = getToken();
		validateToken(token, args[0], args[1]);
		sessionId = retrieveSessionId(token);
		
		return sessionId;
	}

	private String retrieveSessionId(String token) {
		JSONObject postBody = new JSONObject();
		try {
			postBody.put("token", token);
		} catch (JSONException e) {
			// XXX shouldn't be reached; error should've been thrown earlier
			// XXX error putting String token into JSON -> not recoverable
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
		}
		
		JSONObject jsonResponse = new JSONObject();
		
		try {
			HttpPost postRequest = new HttpPost(urls[2]);
			postRequest.setEntity(new StringEntity(postBody.toString()));
			postRequest.setHeader("Content-type", "application/json");
			ResponseHandler<String> handler = new BasicResponseHandler();
			String response = SwengHttpClientFactory.getInstance().
					execute(postRequest, handler);
			jsonResponse = new JSONObject(response);
		} catch (UnsupportedEncodingException e) {
			// XXX local encoding not supported -> not recoverable + nothing to do
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// XXX error in the HTTP protocol -> not recoverable
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
		} catch (IOException e) {
			// XXX general error with server -> not recoverable
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
		} catch (JSONException e) {
			// XXX error while parsing JSONObject
			//		(server answered with corrupted JSON) -> not recoverable
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
		}
		try {
			return jsonResponse.getString("session");
		} catch (JSONException e) {
			// XXX error while looking for "session" in JSON
			//		(Tequila didn't confirm token to SwEng sever) -> not recoverable
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
		}
		// XXX this part of the code should never be reached
		return "";
	}

	private void validateToken(String token, String username, String password) {
		List<NameValuePair> tokenValidationContentList =
			new ArrayList<NameValuePair>();
		tokenValidationContentList.
			add(new BasicNameValuePair("requestkey", token));
		tokenValidationContentList.
			add(new BasicNameValuePair("username", username));
		tokenValidationContentList.
			add(new BasicNameValuePair("password", password));
		
		UrlEncodedFormEntity tokenValidationContentEncoded = null;
		try {
			tokenValidationContentEncoded =
				new UrlEncodedFormEntity(tokenValidationContentList);
		} catch (UnsupportedEncodingException e) {
			// XXX local encoding not supported -> not recoverable + nothing to do
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
		}
		
		HttpPost tokenValidationRequest = new HttpPost(urls[1]);
		tokenValidationRequest.setEntity(tokenValidationContentEncoded);
		
		try {
			HttpResponse response = SwengHttpClientFactory.getInstance().
				execute(tokenValidationRequest);
			if (response.getStatusLine().getStatusCode() != HTTP_STATUS_FOUND) {
				// XXX Tequila rejected usrname/pwd  -> not recoverable + tell to retry
				// TODO Call Aymeric's log architecture and David's errorHandle function
			}
		} catch (ClientProtocolException e) {
			// XXX error in the HTTP protocol -> not recoverable + tell to retry later
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
		} catch (IOException e) {
			// XXX general error with server -> not recoverable + tell to retry later
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
		}
	}

	private String getToken() {
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		HttpGet get = new HttpGet(urls[0]);
		String token = "";
		try {
			String response = SwengHttpClientFactory.getInstance().
					execute(get, responseHandler);
			JSONObject responseJSON = new JSONObject(response);
			token = responseJSON.getString("token");
		} catch (ClientProtocolException e) {
			// XXX error in the HTTP protocol -> not recoverable + tell to retry later
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// XXX general error with server -> not recoverable + tell to retry later
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			// XXX error while parsing JSONObject
			//		(no "token" field or corrupted JSON) -> not recoverable + tell to retry later
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
			return null;
		}
		return token;
	}

	@Override
	protected void onPostExecute(String result) {
		//TODO load session id into Joanna's SharedPref
		dialog.dismiss();
		Toast.makeText(context, "Authentication activity finished, " +
				"session id = " + sessionId, Toast.LENGTH_LONG).show();
	}

}