package pt.isec.cubiqua.model;

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
        this.logMessages.add(message);
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
        int _len = Math.min(logMessages.size()-1, MESSAGE_WINDOW);
        List<String> ret = new ArrayList<>();
        for (int i = _len ; i > 0; i--) {
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
}
