package epfl.sweng.authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Data structure which uses a {@link SharedPreferences} to store the
 * preferences of the user.
 * <p>
 * This class implements the singleton pattern.
 * 
 * @author JoTearoom
 * 
 */
public final class UserPreferences {
	
	private static UserPreferences sSingletonStorage;
	private SharedPreferences mUserCredentialsPrefs;
	private Editor mEditor;
	private String mSharedPreferencesName = "user_session";
	private final String mKeySessionIDName = "SESSION_ID";
	private final String mKeyConnectionState = "CONNECTION_STATE";

	/**
	 * Private constructor of the singleton.
	 * 
	 * @param context
	 *            Context of the activity that needs the storage.
	 */
	
	private UserPreferences(Context context) {
		this.mUserCredentialsPrefs = context.getSharedPreferences(
				mSharedPreferencesName, Context.MODE_PRIVATE);
		this.mEditor = mUserCredentialsPrefs.edit();
		this.createEntry(mKeyConnectionState, "ONLINE");
	}

	/**
	 * Returns the singleton, creates it if it's not instancied.
	 * 
	 * @param context Context of the activity that needs the storage.
	 * @return Singleton instance of the class.
	 */
	
	public static UserPreferences getInstance(
			Context context) {
		// double-checked singleton: avoids calling costly synchronized if
		// unnecessary
		if (null == sSingletonStorage) {
			synchronized (UserPreferences.class) {
				if (null == sSingletonStorage) {
					sSingletonStorage = new UserPreferences(context);
				}
			}
		}
		return sSingletonStorage;
	}

	/**
	 * Singleton getter when no context is available.
	 * @return
	 * 		The singleton instance of this object (may be null!)
	 */
	
	public static UserPreferences getInstance() {
		if (null == sSingletonStorage) {
			Log.e(UserPreferences.class.getName(), "getInstance()"
					+ "without context was used before the one with a Context!");
		}
		return sSingletonStorage;
	}
	
	/**
	 * Create a key value entry for user preferences
	 * 
	 * @param key 
	 * @param value
	 */
	public void createEntry(String key, String value) {
		mEditor.putString(key, value);
		mEditor.commit();
	}

	/**
	 * Checks if the user is authenticated. It's only the case when the table
	 * contains an sessionID.
	 * 
	 * @return <b>true</b> if the sessionID is already in the table.
	 *            <b>false</b> otherwise
	 */
	public boolean isAuthenticated() {
		String value = mUserCredentialsPrefs.getString(mKeySessionIDName, null);
		return value != null;
	}
	
	/**
	 * Checks if the user is connected.
	 * 
	 * @return <b>true</b> if the connectionState is ONLINE,
	 *           <b>false</b> otherwise
	 */
	public boolean isConnected() {
		String value = mUserCredentialsPrefs.getString(mKeyConnectionState, null);
		return value.equals("ONLINE");
	}
	

	/**
	 * Removes the sessionID from the storage table.
	 */
	
	public void destroyAuthentication() {
		mEditor.remove(mKeySessionIDName);
		mEditor.commit();
	}

	/**
	 * Returns the currently stored session ID.
	 * 
	 * @return The value of the storage table.
	 */
	
	public String getSessionId() {
		return mUserCredentialsPrefs.getString(mKeySessionIDName, "NOT LOGGED IN!");
	}
}