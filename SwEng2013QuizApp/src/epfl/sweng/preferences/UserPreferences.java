package epfl.sweng.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import epfl.sweng.app.SwEng2013QuizApp;
import epfl.sweng.comm.ConnectivityProxy;
import epfl.sweng.comm.ConnectivityState;
import epfl.sweng.comm.QuestionProxy;

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
	private ConnectivityState mCurrentConnectivityState;
	private ConnectivityProxy mProxy;

	/**
	 * Singleton getter
	 * 
	 * @return
	 * 		The singleton instance of this object (may be null!)
	 */
	
	public static UserPreferences getInstance() {
		if (null == sSingletonStorage) {
			synchronized (UserPreferences.class) {
				if (null == sSingletonStorage) {
					sSingletonStorage = new UserPreferences();
				}
			}
		}
		return sSingletonStorage;
	}
	
	/**
	 * Create a key value entry for user preferences
	 * 
	 * @param key 
	 * @param value
	 */
	@Deprecated
	public void createEntry(String key, String value) {
		mEditor.putString(key, value);
		mEditor.commit();
	}
	
	public void setSessionId(String sessionId) {
		mEditor.putString(mKeySessionIDName, sessionId);
		mEditor.commit();
	}

	public int setConnectivityState(ConnectivityState newState) {
		mCurrentConnectivityState = newState;
		return mProxy.notifyConnectivityChange(newState);
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
		return mCurrentConnectivityState.equals(ConnectivityState.ONLINE);
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
		if (mUserCredentialsPrefs == null) {
			return "";
		}
		return mUserCredentialsPrefs.getString(mKeySessionIDName, "NOT LOGGED IN!");
	}

	/**
	 * Returns the current connectivity state of the app.
	 * 
	 * @return A copy of the {@link ConnectivityState} describing the current
	 * connectivity state of the application.
	 */
	
	public ConnectivityState getConnectivityState() {
		//this is to avoid modifications through this getter
		return ConnectivityState.valueOf(mCurrentConnectivityState.toString());
	}

	/**
	 * Private constructor of the singleton.
	 * 
	 */
	
	private UserPreferences() {
		this.mUserCredentialsPrefs = SwEng2013QuizApp.getContext().
				getSharedPreferences(mSharedPreferencesName, Context.MODE_PRIVATE);
		this.mEditor = mUserCredentialsPrefs.edit();
		this.mCurrentConnectivityState = ConnectivityState.ONLINE;
		this.mProxy = QuestionProxy.getInstance();
	}
}