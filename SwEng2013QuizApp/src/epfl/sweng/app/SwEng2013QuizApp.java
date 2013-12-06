package epfl.sweng.app;

import android.app.Application;
import android.content.Context;
import android.test.mock.MockContext;

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
			return new MockContext();
		} else {
			return mApplicationContext;
		}
	}
}
