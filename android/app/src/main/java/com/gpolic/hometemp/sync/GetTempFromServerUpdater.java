package com.gpolic.hometemp.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.gpolic.hometemp.data.StatisticsDBController;
import com.gpolic.hometemp.data.TemperaturesDBController;
import com.gpolic.hometemp.logger.LLog;
import com.gpolic.hometemp.model.TemperatureItem;
import com.gpolic.hometemp.model.TemperaturesList;
import com.gpolic.hometemp.util.MyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import javax.net.ssl.HttpsURLConnection;


/**
 * A helpder class to retrieve the temperatures from the server using AsyncTask
 * <p>
 * In progress... </p>
 * Create a post request to send the "date" and the server will return the data from that date onwards.
 * If needed to retrieve all of the database then use date 1/1/1900 or something
 */
public class GetTempFromServerUpdater {

    private static final String TAG = GetTempFromServerUpdater.class.getSimpleName();
    private static final String JSON_ARRAY = "result";
    private static final String TEMPERATURE = "temperature";
    private static final String HUMIDITY = "humidity";
    private static final String TIMESTAMP = "timest";
    private static TemperaturesList tempsFromTheServer = TemperaturesList.getInstance();

    private String myJSONResultString;

    private void extractJSON(Context context) {
        final TemperaturesDBController dbController = new TemperaturesDBController(context);
        String myJSONResult;
//        LLog.d(TAG, "DB Record count: " + databaseHelper.getTempRecordsCount());

        tempsFromTheServer.clear();           // clear the array in case it has already some data
        JSONArray jsonTemperaturesArray;

        // need to fix the Server POST reply after the upgrade on FreeWebHost
        myJSONResult = fixJSONResult(myJSONResultString);

        try {
            JSONObject jsonObject = new JSONObject(myJSONResult);
            jsonTemperaturesArray = jsonObject.getJSONArray(JSON_ARRAY);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            long start = System.nanoTime();

            //TODO try this without creating result objects. Use just primitive vars?
            for (int i = 0; i < jsonTemperaturesArray.length(); i++) {
                JSONObject json_data = jsonTemperaturesArray.getJSONObject(i);
                TemperatureItem resultRow = new TemperatureItem();

                resultRow.setDate(json_data.getString(TIMESTAMP));
                resultRow.setTemperature((float) json_data.getDouble(TEMPERATURE));
                resultRow.setHumidity((float) json_data.getDouble(HUMIDITY));
                tempsFromTheServer.add(resultRow);
            }
            LLog.d(TAG, "extractJSON. Finished inserting " + jsonTemperaturesArray.length() + " records in Temps Collection. Time : " + MyHelper.convertTime(System.nanoTime() - start));

            if (tempsFromTheServer.size() > 0) {
                start = System.nanoTime();
                dbController.insertTempItems(tempsFromTheServer);
                LLog.d(TAG, "extractJSON. Inserted tempItems in local db. Time " + MyHelper.convertTime(System.nanoTime() - start));

                new StatisticsDBController(context).calculateStats();


            } else LLog.d(TAG, "extractJSON. There were no new rows on the server");

        } catch (JSONException e) {
            e.printStackTrace();
            LLog.e(TAG, "JSON Exception");  // TODO
        } catch (Exception ex) {
            ex.printStackTrace();
            LLog.e(TAG, "Exception occurred in extractJson");
        }
    }


    /**
     * Function to fix the JSON results coming from the server. </p>
     * The POST result contains a header with the server description after an upgrade, which is causing JSON exception in the parsing
     */
    private String fixJSONResult(String JsonString) {
        String fixedJsonResult;

        int findResultIndex = JsonString.indexOf("{\"result\"");

        if (findResultIndex > 0) {
            fixedJsonResult = JsonString.substring(findResultIndex);
            return fixedJsonResult;
        } else return "";
    }


    public void getJSON(final Context context, final String url, final String message, final String dataAfterTheDate) {

        class GetJSON extends AsyncTask<String, Void, Boolean> {
            ProgressDialog loading;

            private GetJSON(Context context) {
                // this will allow to present the dialog in the main Activity
                loading = new ProgressDialog(context);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading.setTitle(message);
                loading.show();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                String uri = params[0];
                String dataAfterDate = params[1];
                BufferedReader bufferedReader;

                LLog.d(TAG, "in doInBackground of getJSON task");   // TODO

                try {
                    long start = System.nanoTime(); // TODO

                    URL url = new URL(uri);
                    StringBuilder sb = new StringBuilder();
                    String json;

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");

                    String data = URLEncoder.encode("date", "UTF-8")   // setup the URI for the POST request
                            + "=" + URLEncoder.encode(dataAfterDate, "UTF-8");

                    OutputStream os = con.getOutputStream();

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, Charset.forName("UTF-8")));
                    writer.write(data);

                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        while ((json = bufferedReader.readLine()) != null) {
                            sb.append(json).append("\n");       // read the JSON result line by line
                        }
                        myJSONResultString = sb.toString();
                        if (TextUtils.isEmpty(myJSONResultString)) return false;
                        if (myJSONResultString.length() > 19)
                            Log.e(TAG, myJSONResultString.substring(0, 20)); // log first few characters

                        LLog.d(TAG, myJSONResultString);
                        LLog.d(TAG, "AsyncTask doInBackground. Download data from server. Time taken " + MyHelper.convertTime(System.nanoTime() - start));   // TODO

                        start = System.nanoTime();  // TODO
                        extractJSON(context);
                        LLog.d(TAG, "Extracted Json in TempList. Time taken " + MyHelper.convertTime(System.nanoTime() - start));   // TODO
                    } else {
                        myJSONResultString = "";
                    }
                    return true;

                } catch (Exception e) {
                    StackTraceElement[] exceptionElement = e.getStackTrace();
                    LLog.e(TAG, "Could not read data from server.");
                    for (StackTraceElement el : exceptionElement)
                        LLog.e(TAG, "" + el.getMethodName() + " " + el.getClassName() + " " + el.getLineNumber());  // TODO
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                LLog.d(TAG, "In postExecute. Result of getJSON AsyncTask was :" + result);
                loading.dismiss();
            }
        }       // end of nested class

        GetJSON myJsonTask = new GetJSON(context);    // create an instance of the AsyncTask subclass
        LLog.d(TAG, "Preparing task");   // TODO
        myJsonTask.execute(url, dataAfterTheDate);    // execute async task
    }
}
