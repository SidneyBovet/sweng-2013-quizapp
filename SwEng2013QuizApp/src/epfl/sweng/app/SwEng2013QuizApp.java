package epfl.sweng.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Subclass of our application.
 * 
 * @author Melody Lucid
 *
 */
public class SwEng2013QuizApp extends Application {

	private static Context mApplicationContext = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mApplicationContext = getApplicationContext();
	}
	
	/**
	 * Retrieves the context of our application in a static way.
	 * <p>
	 * Returns <code>null</code> if not available.
	 * 
	 * @return The context, or <code>null</code> if unavailable.
	 */
	
	public static Context getContext() {
		if (mApplicationContext == null) {
			Log.w(SwEng2013QuizApp.class.getName(), "getContext(): Called before "
					+ "the onCreate() method. Returned null.");
			return null;
		} else {
			return mApplicationContext;
		}
	}
}
