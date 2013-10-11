package epfl.sweng.backend;

import epfl.sweng.servercomm.AuthenticatingUnit;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Storage structure which use a SharedPreferences to store the sessionID
 * 
 * @author JoTearoom
 * 
 */
public final class UserCreditentialsStorage {
	private static UserCreditentialsStorage singletonStorage;
	private SharedPreferences userCreditentialsPrefs;
	private Editor editor;
	private String sharedPreferencesName = "user_session";
	private String keySessionIDName = "SESSION_ID";

	// XXX qu'une key et qu'une sessionID = value?

	/**
	 * Constructor which job is to create the storage (using SharePreferences
	 * interface)
	 * 
	 * @param context
	 *            context of the activity that creates the storage
	 */
	private UserCreditentialsStorage(Context context) {
		this.userCreditentialsPrefs = context.getSharedPreferences(
				sharedPreferencesName, 0);
		/*
		 * Operating mode. Use 0 or MODE_PRIVATE for the default operation,
		 * MODE_WORLD_READABLE and MODE_WORLD_WRITEABLE to control permissions.
		 * The bit MODE_MULTI_PROCESS can also be used if multiple processes are
		 * mutating the same SharedPreferences file. MODE_MULTI_PROCESS is
		 * always on in apps targetting Gingerbread (Android 2.3) and below, and
		 * off by default in later versions.
		 */
		this.editor = userCreditentialsPrefs.edit();
	}

	/**
	 * Creation of the singleton
	 * 
	 * @param context
	 * @return Singleton instance of the class
	 */
	public static UserCreditentialsStorage getSingletonInstanceOfStorage(
			Context context) {
		// double-checked singleton: avoids calling costly synchronized if
		// unnecessary
		if (null == singletonStorage) {
			synchronized (UserCreditentialsStorage.class) {
				if (null == singletonStorage) {
					singletonStorage = new UserCreditentialsStorage(context);
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
	public void takeAuthentification(int sessionID) {
		editor.putInt(keySessionIDName, sessionID);
		/*
		 * Once you have changed the value you have to call the apply() method
		 * to apply your asynchronously to the file system. The usage of the
		 * commit() method is discouraged, as it write the changes synchronously
		 * to the file system.
		 */
		editor.commit();
	}

	/**
	 * Check if the sessionID belongs to the table or not
	 * 
	 * @param sessionID
	 *            that is checked to be in the table.
	 * @return yes if the sessionID is already in the table
	 */
	public boolean isAuthentificated(int sessionID) {
		// XXX defvalue int -1 or 0?
		int value = userCreditentialsPrefs.getInt(keySessionIDName, -1);
		// XXX revient au meme que value!=-1?
		return value == sessionID;
	}

	/**
	 * Remove the sessionID from the storage table
	 * 
	 * @param sessionID
	 *            that we want to remove because the user logged out
	 */
	public void releaseAuthentification(int sessionID) {
		editor.remove(keySessionIDName);
		editor.commit();
	}

}