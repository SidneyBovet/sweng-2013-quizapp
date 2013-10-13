package epfl.sweng.backend;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Storage structure which use a SharedPreferences to store the sessionID
 * 
 * @author JoTearoom
 * 
 */
public final class UserCredentialsStorage {
	private static UserCredentialsStorage singletonStorage;
	private SharedPreferences userCredentialsPrefs;
	private Editor editor;
	private String sharedPreferencesName = "user_session";
	private final String keySessionIDName = "SESSION_ID";

	/**
	 * Constructor which job is to create the storage (using SharePreferences
	 * interface)
	 * 
	 * @param context
	 *            context of the activity that creates the storage
	 */
	private UserCredentialsStorage(Context context) {
		this.userCredentialsPrefs = context.getSharedPreferences(
				sharedPreferencesName, Context.MODE_PRIVATE);
		this.editor = userCredentialsPrefs.edit();
	}

	/**
	 * Creation of the singleton
	 * 
	 * @param context
	 * @return Singleton instance of the class
	 */
	public static UserCredentialsStorage getSingletonInstanceOfStorage(
			Context context) {
		// double-checked singleton: avoids calling costly synchronized if
		// unnecessary
		if (null == singletonStorage) {
			synchronized (UserCredentialsStorage.class) {
				if (null == singletonStorage) {
					singletonStorage = new UserCredentialsStorage(context);
				}
			}
		}
		return singletonStorage;
	}

	/**
	 * Put the sessionID in the storage table
	 * 
	 * @param sessionID
	 *            that the user received from Tequila
	 */
	public void takeAuthentication(String sessionID) {
		editor.putString(keySessionIDName, sessionID);
		editor.commit();
	}

	/**
	 * Check if the table contains a sessionID
	 * 
	 * @param sessionID
	 *            that is checked to be in the table.
	 * @return yes if the sessionID is already in the table
	 */
	public boolean isAuthenticated() {
		String value = userCredentialsPrefs.getString(keySessionIDName, null);
		return value != null;
	}

	/**
	 * Remove the sessionID from the storage table
	 * 
	 * @param sessionID
	 *            that we want to remove because the user logged out
	 */
	public void releaseAuthentication() {
		editor.remove(keySessionIDName);
		editor.commit();
	}

}