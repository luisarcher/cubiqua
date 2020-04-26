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

    private void init() {

    }

    public String getServerFileAddress() {
        return sharedPreferences.getString(SERVER_FILE_ADDRESS, "");
    }


    /* vvv to be removed vvv */

    public boolean isSetSessId() {
        return sharedPreferences.contains("sessId");
    }

    public void setSessId(int id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("sessId", id);
        editor.apply();
    }

    public int getSessId() {
        return sharedPreferences.getInt("sessId", 0);
    }

    public void incSessionId() {
        if (this.isSetSessId()) {
            this.setSessId(this.getSessId() + 1);
        } else {
            this.setSessId(0);
        }
    }
}
