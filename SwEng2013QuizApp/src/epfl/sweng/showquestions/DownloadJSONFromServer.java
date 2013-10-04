package epfl.sweng.showquestions;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import android.os.AsyncTask;
import epfl.sweng.servercomm.SwengHttpClientFactory;

public class DownloadJSONFromServer extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... url) {
		HttpGet firstRandom = new HttpGet(url[0]);
		ResponseHandler<String> firstHandler = new BasicResponseHandler();
		String randomQuestionJSON = null;
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