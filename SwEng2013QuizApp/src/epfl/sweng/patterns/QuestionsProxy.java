package epfl.sweng.patterns;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;

import android.util.Log;
import epfl.sweng.authentication.UserPreferences;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.servercomm.HttpFactory;
import epfl.sweng.servercomm.SwengHttpClientFactory;

/**
 * This class will perform all the server interactions
 * in the place of our app. It will also cache all the 
 * questions that we fetch from the server and take place
 * of the server when in offline mode.
 * 
 * XXX If we don't use a Singleton, how do we ensure that
 * we only have one proxy class? Do we have a boolean flag
 * that tells us if it has been instanciated?
 *  
 *  
 * @author born4new, JoTearoom, Merok
 *
 */
public final class QuestionsProxy {
	
	private static QuestionsProxy sQuestionProxy;
	//question to be sent
	private List<QuizQuestion> mQuizzQuestionsOutbox;
	//question to be retrieve
	private List<QuizQuestion> mQuizzQuestionsInbox;
	private UserPreferences mUserPreferences;
	
	/**
	 * Private constructor of the singleton.
	 * 
	 */
	private QuestionsProxy() {
		mQuizzQuestionsOutbox = new ArrayList<QuizQuestion>();
		mQuizzQuestionsInbox = new ArrayList<QuizQuestion>();
		mUserPreferences = UserPreferences.getInstance();
	}

	/**
	 * Returns the singleton, creates it if it's not instancied.
	 * 
	 * @return Singleton instance of the class.
	 */
	
	public static QuestionsProxy getInstance() {
		// double-checked singleton: avoids calling costly synchronized if
		// unnecessary
		if (null == sQuestionProxy) {
			synchronized (QuestionsProxy.class) {
				if (null == sQuestionProxy) {
					sQuestionProxy = new QuestionsProxy();
				}
			}
		}
		return sQuestionProxy;
	}

	/**
	 * Add a {@link QuizQuestion} to the Inbox only if it is a well 
	 * formed question
	 * @param question The {@link QuizQuestion} to be verify
	 */
	public void addInbox(QuizQuestion question) {
		if (question.auditErrors() == 0) {
			mQuizzQuestionsInbox.add(question);
		}
	}
	
	/**
	 * Add a {@link QuizQuestion} to the Outbox only if it is a well 
	 * formed question
	 * @param question The {@link QuizQuestion} to be verify
	 */
	public void addOutbox(QuizQuestion question) {
		if (question.auditErrors() == 0) {
			mQuizzQuestionsOutbox.add(question);
		}
	}
	
	/**
	 * Send a {@link QuizQuestion} to the server after having stored 
	 * it in the cache and send the cached questions to be sent if online.
	 * Store the {@link QuizQuestion} to be sent in the cache if offline.
	 * @param question {@link QuizQuestion} that we want to send
	 */
	public int sendQuizzQuestion(QuizQuestion question) {
		
		addInbox(question);
		
		int responseStatus = -1;
		if (mUserPreferences.isConnected()) {
			
			HttpPost post = HttpFactory.getPostRequest(
					HttpFactory.getSwengBaseAddress() + "/quizquestions/");

			try {
				post.setEntity(new StringEntity(question.toJSON().toString()));
				post.setHeader("Content-type", "application/json");
				HttpResponse mResponse = SwengHttpClientFactory.getInstance().execute(post);
				responseStatus = mResponse.getStatusLine().getStatusCode();
			} catch (UnsupportedEncodingException e) {
				// XXX switch to off line mode
				Log.e(this.getClass().getName(), "doInBackground(): Entity does "
						+ "not support the local encoding.", e);
			} catch (ClientProtocolException e) {
				// XXX switch to off line mode
				Log.e(this.getClass().getName(), "doInBackground(): Error in the "
						+ "HTTP protocol.", e);
			} catch (IOException e) {
				// XXX switch to off line mode
				Log.e(this.getClass().getName(), "doInBackground(): An I/O error "
						+ "has occurred.", e);
			}
			
			/*while (mQuizzQuestionsOutbox.size() > 0) {
				QuizQuestion mquestionOut = mQuizzQuestionsOutbox.remove(0);
				ServerInteractions.submitQuestion(mquestionOut);
				//XXX pour l'instant pas dans le bon ordre => regler asynctask
			}*/
		} else {
			addOutbox(question);
		}
		
		return responseStatus;
	}
	
	/**
	 * Retrieve a {@link QuizQuestion} from the server and store it in the 
	 * cache before returning it if online.
	 * Choose a random {@link QuizQuestion} from the cached content before
	 * returning it if offline.
	 * @return {@link QuizQuestion} retrieve from the server 
	 */
	public QuizQuestion retrieveQuizzQuestion() {
		
		QuizQuestion fetchedQuestion = null;
		
		if (mUserPreferences.isConnected()) {
			// XXX Why don't we use one method for these two calls instead
			// of giving URL to the getGetRequest?
			String url = HttpFactory.getSwengFetchQuestion();
			HttpGet firstRandom = HttpFactory.getGetRequest(url);
			try {
				String jsonQuestion = SwengHttpClientFactory.getInstance().
						execute(firstRandom, new BasicResponseHandler());
				fetchedQuestion = new QuizQuestion(jsonQuestion);
				addInbox(fetchedQuestion);
			} catch (ClientProtocolException e) {
				Log.e(this.getClass().getName(), "doInBackground(): Error in"
						+ "the HTTP protocol.", e);
				// TODO switch to off line mode
			} catch (IOException e) {
				Log.e(this.getClass().getName(), "doInBackground(): An I/O"
						+ "error has occurred.", e);
				// TODO switch to off line mode
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		} else {
			//TODO gerer cas on commence en mode offline donc liste vide
			int questionIDCache = new Random()
				.nextInt(mQuizzQuestionsInbox.size());
			fetchedQuestion = mQuizzQuestionsInbox.get(questionIDCache);
		}
		
		return fetchedQuestion;
	}
}
