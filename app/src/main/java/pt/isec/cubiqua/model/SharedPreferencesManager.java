package pt.isec.cubiqua.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SharedPreferencesManager {

    /* ***** FILE SERVER ***** */
    public static final String PREF_KEY_SERVER_FILE_ADDRESS = "server_file_address";
    public static final String PREF_KEY_SERVER_FILE_USERNAME = "server_file_username";
    public static final String PREF_KEY_SERVER_FILE_PASSWORD = "server_file_password";

    public static final String PREF_DEF_VAL_SERVER_FILE_ADDRESS = "urbysense.dei.uc.pt";
    public static final String PREF_DEF_VAL_SERVER_FILE_USERNAME = "isecalumni";
    public static final String PREF_DEF_VAL_SERVER_FILE_PASSWORD = ""; //"miscubi1920"

    /* ***** DATABASE SERVER ***** */
    public static final String PREF_KEY_SERVER_DATABASE_ADDRESS = "server_db_address";
    public static final String PREF_KEY_SERVER_DATABASE_NAME = "server_db_name";
    public static final String PREF_KEY_SERVER_DATABASE_USERNAME = "server_db_username";
    public static final String PREF_KEY_SERVER_DATABASE_PASSWORD = "server_db_password";

    public static final String PREF_DEF_VAL_SERVER_DATABASE_ADDRESS = "";
    public static final String PREF_DEF_VAL_SERVER_DATABASE_NAME = "";
    public static final String PREF_DEF_VAL_SERVER_DATABASE_USERNAME = "postgres";
    public static final String PREF_DEF_VAL_SERVER_DATABASE_PASSWORD = "postgres";



    private SharedPreferences sharedPreferences;

    public SharedPreferencesManager(Context c) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
    }

    /* ***** FILE SERVER ***** */
    public String getServerFileAddress() {
        return sharedPreferences.getString(PREF_KEY_SERVER_FILE_ADDRESS, PREF_DEF_VAL_SERVER_FILE_ADDRESS);
    }

    public String getServerFileUsername() {
        return sharedPreferences.getString(PREF_KEY_SERVER_FILE_USERNAME, PREF_DEF_VAL_SERVER_FILE_USERNAME);
    }

    public String getServerFilePassword() {
        return sharedPreferences.getString(PREF_KEY_SERVER_FILE_PASSWORD, PREF_DEF_VAL_SERVER_FILE_PASSWORD);
    }

    /* ***** DATABASE SERVER ***** */
    public boolean isDBSync() {
        return sharedPreferences.getBoolean("db_sync", false);
    }

    public String getServerDatabaseAddress() {
        return sharedPreferences.getString(PREF_KEY_SERVER_DATABASE_ADDRESS, PREF_DEF_VAL_SERVER_DATABASE_ADDRESS);
    }

    public String getServerDatabaseName() {
        return sharedPreferences.getString(PREF_KEY_SERVER_DATABASE_NAME, PREF_DEF_VAL_SERVER_DATABASE_NAME);
    }

    public String getServerDatabaseUsername() {
        return sharedPreferences.getString(PREF_KEY_SERVER_DATABASE_USERNAME, PREF_DEF_VAL_SERVER_DATABASE_USERNAME);
    }

    public String getServerDatabasePassword() {
        return sharedPreferences.getString(PREF_KEY_SERVER_DATABASE_PASSWORD, PREF_DEF_VAL_SERVER_DATABASE_PASSWORD);
    }



}
