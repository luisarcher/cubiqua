package pt.isec.cubiqua.model;

import android.icu.util.Calendar;

import java.util.ArrayList;
import java.util.List;

import pt.isec.cubiqua.ui.IOnNewMessageListener;

public class AppLog {

    private static final int MESSAGE_WINDOW = 10;
    private static AppLog _instance = null;

    private List<String> logMessages;
    private List<IOnNewMessageListener> listeners;

    private AppLog() {
        this.logMessages = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    public static AppLog getInstance() {
        if (_instance == null)
            _instance = new AppLog();
        return _instance;
    }
    public static AppLog Singleton() {
        if (_instance == null)
            _instance = new AppLog();
        return _instance;
    }

    public void log(String message) {
        this.logMessages.add(getTimeStamp() + ": " + message);
        this.notifyListeners();
    }

    public List<String> getMessages() {
        return this.logMessages;
    }

    /*public List<String> getLastMessages() {
        if (logMessages.size() <= MESSAGE_WINDOW) {
            return logMessages;
        }
        return logMessages.subList(logMessages.size() - MESSAGE_WINDOW, logMessages.size());
    }*/

    public List<String> getLastMessages() {

        int _lim = (logMessages.size() > MESSAGE_WINDOW ? logMessages.size() - MESSAGE_WINDOW : 0);
        List<String> ret = new ArrayList<>();
        for (int i = logMessages.size()-1 ; i >= _lim; i--) {
            ret.add(logMessages.get(i));
        }
        return ret;
    }

    public void addListener(IOnNewMessageListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(IOnNewMessageListener listener) {
        this.listeners.remove(listener);
    }

    private void notifyListeners() {
        for (IOnNewMessageListener listener : this.listeners) {
            listener.onNewMessage(logMessages.get(logMessages.size() - 1));
        }
    }

    private String getTimeStamp(){
        int _hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String hour = (_hour < 10 ? "0" + _hour : "" + _hour);
        int _min = Calendar.getInstance().get(Calendar.MINUTE);
        String min = (_min < 10 ? "0" + _min : "" + _min);
        int _sec = Calendar.getInstance().get(Calendar.SECOND);
        String sec = (_sec < 10 ? "0" + _sec : "" + _sec);
        return "" + hour + min + sec;
    }
}
