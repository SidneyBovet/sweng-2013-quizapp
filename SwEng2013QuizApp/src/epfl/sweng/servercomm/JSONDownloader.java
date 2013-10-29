//package epfl.sweng.servercomm;
//
//import java.io.IOException;
//
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.ResponseHandler;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.BasicResponseHandler;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
///**
// * Asyncronous task that runs a background thread that fetches a new 
// * {@link Question} from the SwEng server.
// * <p>
// * The task returns an empty string if a problem occurred during the
// * transaction.
// * 
// * @author Joanna
// * 
// */
//public class JSONDownloader extends AsyncTask<String, Void, String> {
//	
//	@Override
//	protected String doInBackground(String... url) {
//		String randomQuestionJSON = "";
//		if (null != url && 0 != url.length) {
//			HttpGet firstRandom = HttpFactory.getGetRequest(url[0]);
//			ResponseHandler<String> firstHandler = new BasicResponseHandler();
//			try {
//				randomQuestionJSON = SwengHttpClientFactory.getInstance().
//						execute(firstRandom, firstHandler);
//			} catch (ClientProtocolException e) {
//				Log.e(this.getClass().getName(), "doInBackground(): Error in"
//						+ "the HTTP protocol.", e);
//				// TODO error handling
//			} catch (IOException e) {
//				Log.e(this.getClass().getName(), "doInBackground(): An I/O"
//						+ "error has occurred.", e);
//				// TODO error handling
//			}
//		}
//		return randomQuestionJSON;
//	}
//	
//}
