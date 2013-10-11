package epfl.sweng.servercomm;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class AuthenticationProcess extends AsyncTask<String, Void, String> {
	private final String[] urls = {
		"https://sweng-quiz.appspot.com/login",
		"https://tequila.epfl.ch/cgi-bin/tequila/login",
		"https://sweng-quiz.appspot.com/login"
	};
	
	@Override
	protected String doInBackground(String... arg) {
		String sessionId = "";
		if (arg.length != 0) {
			System.err.println("Illegal arguments given to " +
					"AutehnticationProcess, should be (usrname, pwd)");
			return null;
		}
		
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		HttpGet get = new HttpGet(urls[0]);
		String token = "";
		try {
			String response = SwengHttpClientFactory.getInstance().
					execute(get, responseHandler);
			JSONObject responseJSON = new JSONObject(response);
			token = responseJSON.getString("token");
		} catch (ClientProtocolException e) {
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			// TODO Call Aymeric's log architecture and David's errorHandle function
			e.printStackTrace();
			return null;
		}
		
		System.out.println("Token retrieved from server: "+token);
		
		
		return sessionId;
	}

	@Override
	protected void onPostExecute(String result) {
		
		//TODO load session id into Joanna's SharedPref
	}

}
