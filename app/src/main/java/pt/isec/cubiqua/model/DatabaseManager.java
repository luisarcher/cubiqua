package pt.isec.cubiqua.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private Connection conn;
    private Context context;

    public DatabaseManager(Context c) {
        this.context = c;
    }

    private void connect() {

        String username = "postgres";
        String passwd = "postgres";

        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://192.168.56.175:5432/cubdb";
            this.conn = DriverManager.getConnection(url, username, passwd);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            //conn.close();
        }
    }



    private void insertNewRecord(String sqlQuery) {
        this.connect();
        try {
            Statement st = this.conn.createStatement();
            st.executeQuery(sqlQuery);
            st.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.terminate();
    }

    public void insertRecordAsync(String sess_id, String activity, long instant,
                                  double lat, double lon, double alt, boolean indoor,
                                  float x_acc, float y_acc, float z_acc,
                                  float x_gyro, float y_gyro, float z_gyro,
                                  float x_mag, float y_mag, float z_mag) {
        String sqlQuery = parseAsSqlQuery(sess_id, activity, instant, lat, lon, alt, indoor, x_acc, y_acc, z_acc, x_gyro, y_gyro, z_gyro, x_mag, y_mag, z_mag);
        new LongOperation(this.context, this, sqlQuery).execute();
    }

    private String parseAsSqlQuery(String sess_id, String activity, long instant,
                                double lat, double lon, double alt, boolean indoor,
                                float x_acc, float y_acc, float z_acc,
                                float x_gyro, float y_gyro, float z_gyro,
                                float x_mag, float y_mag, float z_mag) {
        return "INSERT INTO public.records(\n" +
                "\tsession_id, activity, instant," +
                " latitude, longitude, altitude, indoor, " +
                " x_acc, y_acc, z_acc," +
                " x_gyro, y_gyro, z_gyro," +
                " x_mag, y_mag, z_mag," +
                " \"position\")\n" +
                "\tVALUES ("+ sess_id +", '"+ activity +"', "+ instant +"," +
                " "+ lat +", "+ lon +" , "+ alt +"," + indoor + ", " +
                " "+ x_acc +", "+ y_acc +", "+ z_acc +"," +
                " "+ x_gyro +", "+ y_gyro +", "+ z_gyro +"," +
                " "+ x_mag +", "+ y_mag +", "+ z_mag +"," +
                " ST_SetSRID(ST_Point("+ lon +","+ lat +"),4326));";
    }

    private void terminate() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class LongOperation extends AsyncTask<Void, Integer, String> {

        private final Context context;
        private ProgressDialog progress;
        private DatabaseManager classRef;
        private String sqlQuery;


        LongOperation(Context c, DatabaseManager ref, String sqlQuery) {
            this.context = c;
            this.classRef = ref;
            this.sqlQuery = sqlQuery;
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading...");
            progress.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            this.classRef.insertNewRecord(this.sqlQuery);
            return "ok";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("PostExecuted", result);
            progress.dismiss();
        }
    }

    /*public void insertRecordTest() {

        this.connect();

        String sess_id = "2";
        String activity = "test_from_app";
        String instant = "1587493112";

        String lat = "40.1929798";
        String lon = "-8.4046458";
        String alt = "100.1999969";
        boolean indoor = false;

        String x_acc = "0.2935547";
        String y_acc = "-0.2894287";
        String z_acc = "7.9598513";

        String x_gyro = "0.0000610";
        String y_gyro = "-0.000443";
        String z_gyro = "0.0000458";

        String x_mag = "-14.631653";
        String y_mag = "-20.829773";
        String z_mag = "-23.017883";

        String sqlQuery = "INSERT INTO public.records(\n" +
                "\tsession_id, activity, instant," +
                " latitude, longitude, altitude, indoor, " +
                " x_acc, y_acc, z_acc," +
                " x_gyro, y_gyro, z_gyro," +
                " x_mag, y_mag, z_mag," +
                " \"position\")\n" +
                "\tVALUES ("+ sess_id +", '"+ activity +"', "+ instant +"," +
                " "+ lat +", "+ lon +" , "+ alt +"," + indoor + ", " +
                " "+ x_acc +", "+ y_acc +", "+ z_acc +"," +
                " "+ x_gyro +", "+ y_gyro +", "+ z_gyro +"," +
                " "+ x_mag +", "+ y_mag +", "+ z_mag +"," +
                " ST_SetSRID(ST_Point("+ lon +","+ lat +"),4326));";

        try {
            Statement st = this.conn.createStatement();
            ResultSet rs = st.executeQuery(sqlQuery);
            Log.d(DatabaseManager.class.getName(), rs.toString());

            rs.close();
            st.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.terminate();
    }*/

    /*public void insertRecordTestAsync() {
        new LongOperation(this.context, this).execute();
    }*/
}
