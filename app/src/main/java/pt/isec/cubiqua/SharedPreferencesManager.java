package pt.isec.cubiqua;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesManager {

    private SharedPreferences sharedPreferences;

    public SharedPreferencesManager(Context c) {
        this.sharedPreferences = c.getSharedPreferences("cub", MODE_PRIVATE);
    }

    private void init() {

    }

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
