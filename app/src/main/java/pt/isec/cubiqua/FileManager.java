package pt.isec.cubiqua;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

public class FileManager {

    /*  TODO save to local storage*/
    /* TODO send to remote server */
    private Context context;

    private static String FILENAME = "sensor_data";
    private static String FILE_EXTENSION = ".txt";

    public FileManager(Context context){
        this.context = context;
    }

    public void saveFile(String data){
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        if ( mExternalStorageAvailable && mExternalStorageWriteable ){
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), FILENAME + FILE_EXTENSION);
            BufferedWriter bw;
            try {
                bw = new BufferedWriter(new FileWriter(file, true));
                bw.write(data);
                bw.newLine();
                bw.close();
                //bw.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //Nao esquecer:  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        }
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(FILENAME + FILE_EXTENSION);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("Sensor data file", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Sensor data file", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void uploadFile() {
        new LongOperation(this.context).execute();
    }

    private static class LongOperation extends AsyncTask<Void, Integer, String> {

        private final Context context;
        ProgressDialog progress;

        LongOperation(Context c){
            this.context = c;
        }

        @Override
        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage("Loading...");
            progress.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                Date now = new Date();

                JSch ssh = new JSch();
                Session session = ssh.getSession("isecalumni", "urbysense.dei.uc.pt", 22);
                // Remember that this is just for testing and we need a quick access, you can add an identity and known_hosts file to prevent
                // Man In the Middle attacks
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.setPassword("miscubi1920");

                session.connect();
                Channel channel = session.openChannel("sftp");
                channel.connect();

                ChannelSftp sftp = (ChannelSftp) channel;

                sftp.cd("data");
                sftp.cd("a21201026_a2019107030");
                sftp.cd("dev_phase");
                //a21201026_a2019107030

                // /data/a21201026_a2019107030/dev_phase
                // If you need to display the progress of the upload, read how to do it in the end of the article

                // use the put method , if you are using android remember to remove "file://" and use only the relative path
                sftp.put(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ "/" + FILENAME + FILE_EXTENSION, FILENAME + "_" + now.getTime() + FILE_EXTENSION);
                channel.disconnect();
                session.disconnect();
            } catch (JSchException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            } catch (SftpException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            return "Upload finished";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("PostExecuted", result);
            progress.dismiss();
        }

    }
}
