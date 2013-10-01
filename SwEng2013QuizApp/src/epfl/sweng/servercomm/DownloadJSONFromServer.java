package epfl.sweng.servercomm;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import epfl.sweng.backend.Question;

import android.os.AsyncTask;

/**
 * Runs a background thread that fetches a new {@link Question} from the SwEng
 * sever.
 * @author Joanna
 * 
 */
public class DownloadJSONFromServer extends AsyncTask<String, Void, String> {
	
	@Override
	protected String doInBackground(String... url) {
		String randomQuestionJSON = "";
		if (url.length != 0 && url != null) {
			HttpGet firstRandom = new HttpGet(url[0]);
			ResponseHandler<String> firstHandler = new BasicResponseHandler();
			try {
				randomQuestionJSON = SwengHttpClientFactory.getInstance().
						execute(firstRandom, firstHandler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return randomQuestionJSON;
	}
	

}
