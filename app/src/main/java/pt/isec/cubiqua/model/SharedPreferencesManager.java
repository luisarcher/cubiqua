package pt.isec.cubiqua.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SharedPreferencesManager {

    public static final String SERVER_FILE_ADDRESS = "server_file_address";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesManager(Context c) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
    }

    public String getServerFileAddress() {
        return sharedPreferences.getString(SERVER_FILE_ADDRESS, "");
    }

    public boolean isDBSync() {
        return sharedPreferences.getBoolean("db_sync", false);
    }
}
