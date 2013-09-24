package epfl.sweng.showquestions;

import java.io.IOException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONObject;

import epfl.sweng.servercomm.SwengHttpClientFactory;

import android.os.AsyncTask;

public class DownloadJSONFromServer extends AsyncTask<Void, Void, String>{

	@Override
	protected String doInBackground(Void... params) {
		HttpGet firstRandom = new HttpGet("https://sweng-quiz.appspot.com/quizquestions/random");
		ResponseHandler<String> firstHandler = new BasicResponseHandler();
		String randomQuestionJSON = "";
		try {
			randomQuestionJSON = SwengHttpClientFactory.getInstance().execute(firstRandom, firstHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return randomQuestionJSON;
	}
	

}
