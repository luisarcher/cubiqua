package pt.isec.cubiqua.model;

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
import java.util.List;
import java.util.Vector;

import pt.isec.cubiqua.recognition.WekaDataProcessor;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import static com.jcraft.jsch.ChannelSftp.SSH_FX_NO_SUCH_FILE;
import static pt.isec.cubiqua.Consts.FFT_N_READS;

public class FileManagerV2 {

    private Context context;

    private static String FILENAME = "sensor_data_v3";
    private static String FILE_EXTENSION_CSV = ".csv";
    private static String FILE_EXTENSION_ARFF = ".arff";

    SharedPreferencesManager sharedPreferencesManager;

    public FileManagerV2(Context context){
        this.context = context;
        this.sharedPreferencesManager = new SharedPreferencesManager(this.context);
    }

    public String generateCSVHeaderRow(int numAttributes) {
        StringBuilder _out = new StringBuilder();
        for (int i = 1; i <= numAttributes ; i++){
            _out.append("acc").append(i).append(",");
            _out.append("gyro").append(i).append(",");
        }
        _out.append("acc_max").append(",");
        _out.append("gyro_max").append(",");
        _out.append("tag");
        return _out.toString();
    }

    public void saveCurrentFeatures(List<SensorStamp> bufferedData) {

        if (bufferedData.size() >= FFT_N_READS) {
            WekaDataProcessor wekaDataProcessor = new WekaDataProcessor();
            // Simulate a FFT_N_READS window
            for (int i = 0; i < bufferedData.size()-FFT_N_READS ; i++  ){
                List<SensorStamp> view = bufferedData.subList(i, i+FFT_N_READS);
                wekaDataProcessor.execDataAnalysis(view);
            }

            if (wekaDataProcessor.getAllTimeAccFFTData().size()
                    == wekaDataProcessor.getAllTimeGyroFFTData().size()) {

                StringBuilder _out = new StringBuilder();
                int len = wekaDataProcessor.getAllTimeAccFFTData().size();

                for (int i = 0; i < len; i++) {
                    for (int j = 0; j < FFT_N_READS; j++){
                        _out.append(wekaDataProcessor.getAllTimeAccFFTData().get(i)[j]).append(",");
                        _out.append(wekaDataProcessor.getAllTimeGyroFFTData().get(i)[j]).append(",");
                    }
                    _out.append(wekaDataProcessor.getAllTimeAccMax().get(i)).append(",");
                    _out.append(wekaDataProcessor.getAllTimeGyroMax().get(i)).append(",");
                    _out.append(bufferedData.get(0).getTag());

                    if (i < len-1) _out.append("\n");
                }
                this.saveFileAsync(_out.toString());

                /*WekaClassifier wekaClassifier = new WekaClassifier();
                wekaClassifier.bulkPredict(
                        wekaDataProcessor.getAllTimeAccFFTData(),
                        wekaDataProcessor.getAllTimeGyroFFTData(),
                        wekaDataProcessor.getAllTimeAccMax(),
                        wekaDataProcessor.getAllTimeGyroMax()
                );*/

                wekaDataProcessor.clearAllData();
            }
        }
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
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), FILENAME + FILE_EXTENSION_CSV
            );
            boolean fileExists = file.exists();

            BufferedWriter bw;
            try {
                bw = new BufferedWriter(new FileWriter(file, true));
                if (!fileExists) {
                    //bw = new BufferedWriter(new FileWriter(file, false));
                    bw.write(generateCSVHeaderRow(FFT_N_READS));
                    bw.newLine();
                } else {

                }
                bw.write(data);
                bw.newLine();
                bw.close();
                //bw.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //Nao esquecer:  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

            // Converter ficheiro criado para Arff
            //######################################
            // //######################################
            // //#################################################this.convertCSVtoArff(FILENAME);
            //######################################
        }
    }

    public void deleteFile() {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), FILENAME + FILE_EXTENSION_CSV
        );

        if(file.delete()) {
            Log.d("FileManager - deleteFile()","File deleted successfully");
        } else {
            Log.d("FileManager - deleteFile()","Failed to delete the file");
        }
    }

    private void convertCSVtoArff(String file_name) {

        try {
            // load CSV
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),file_name + FILE_EXTENSION_CSV));
            Instances data = loader.getDataSet();

            // save ARFF
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),file_name + FILE_EXTENSION_ARFF));
            saver.setDestination(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),file_name + FILE_EXTENSION_ARFF));
            saver.writeBatch();

            Log.d(FileManagerV2.class.getName(),"ARFF File saved!");

        }
        catch (FileNotFoundException e) {
            Log.e("(convertCSVtoArff) Sensor data file", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("(convertCSVtoArff) Sensor data file", "Can not read file: " + e.toString());
        }
    }

    public void uploadFile() {
        new LongOperationSend(this.context, this).execute();
    }

    public void saveFileAsync(String data) {
        new LongOperationSave(this.context, this, data).execute();
    }

    public void convertCSVtoArffAsync() {
        new LongOperationConvertToARFF(this.context, this, FILENAME + FILE_EXTENSION_CSV).execute();
    }

    // Connecto to SSH and upload file | File Server
    private static class LongOperationSend extends AsyncTask<Void, Integer, String> {

        private final Context context;
        private ProgressDialog progress;
        private FileManagerV2 classRef;


        LongOperationSend(Context c, FileManagerV2 ref){
            this.context = c;
            this.classRef = ref;
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

                String _serverAddress = this.classRef.sharedPreferencesManager.getServerFileAddress();
                String _serverUsername = this.classRef.sharedPreferencesManager.getServerFileUsername();
                String _serverPasswd = this.classRef.sharedPreferencesManager.getServerFilePassword();

                Date now = new Date();

                JSch ssh = new JSch();
                Session session = ssh.getSession(_serverUsername, _serverAddress, 22);
                // Remember that this is just for testing and we need a quick access, you can add an identity and known_hosts file to prevent
                // Man In the Middle attacks
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.setPassword(_serverPasswd);

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
                String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ "/" + FILENAME + FILE_EXTENSION_CSV;
                String dstFileName = FILENAME + "_" + now.getTime() + FILE_EXTENSION_CSV;
                sftp.put(fileName, dstFileName);

                // Make sure the file was uploaded before deleting locally
                if (exists(sftp, dstFileName)) {
                    classRef.deleteFile();
                }

                channel.disconnect();
                session.disconnect();
            } catch (JSchException e) {
                //System.out.println(e.getMessage());
                //e.printStackTrace();
            } catch (SftpException e) {
                //System.out.println(e.getMessage());
                //e.printStackTrace();
            }
            return "Upload finished";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("PostExecuted", result);
            progress.dismiss();
        }

        private static boolean exists(ChannelSftp channelSftp, String path) {
            Vector res = null;
            try {
                res = channelSftp.ls(path);
            } catch (SftpException e) {
                if (e.id == SSH_FX_NO_SUCH_FILE) {
                    return false;
                }
                Log.d("","Unexpected exception during ls files on sftp: [{"+e.id+"}:{"+e.getMessage()+"}]");
            }
            return res != null && !res.isEmpty();
        }
    }

    // Save data Async (after pressing stop)
    private static class LongOperationSave extends AsyncTask<Void, Integer, String> {

        private Context context;
        private ProgressDialog progress;
        private FileManagerV2 classRef;
        private String data;

        LongOperationSave(Context c, FileManagerV2 ref, String data) {
            this.context = c;
            this.classRef = ref;
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Saving data...");
            progress.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            this.classRef.saveFile(this.data);
            return "ok";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("PostExecuted", result);
            progress.dismiss();
        }
    }

    // Save data Async (after pressing stop)
    private static class LongOperationConvertToARFF extends AsyncTask<Void, Integer, String> {

        private Context context;
        private ProgressDialog progress;
        private FileManagerV2 classRef;

        LongOperationConvertToARFF(Context c, FileManagerV2 ref, String data) {
            this.context = c;
            this.classRef = ref;
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Saving data...");
            progress.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            this.classRef.convertCSVtoArff(FILENAME);
            return "ok";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("PostExecuted", result);
            progress.dismiss();
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(FILENAME + FILE_EXTENSION_CSV);

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
}
