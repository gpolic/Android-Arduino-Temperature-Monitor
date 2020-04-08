package com.gpolic.hometemp.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.gpolic.hometemp.adapters.RecyclerAdapterWithSetValues;
import com.gpolic.hometemp.data.TemperaturesDBController;
import com.gpolic.hometemp.logger.LLog;
import com.gpolic.hometemp.model.TemperatureItem;
import com.gpolic.hometemp.util.DateUtils;
import com.gpolic.hometemp.util.MyHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by George on 21/8/2017.
 */

public class GetTempsLocalDBUpdater {
    private static final String TAG = GetTempsLocalDBUpdater.class.getSimpleName();
    private List<TemperatureItem> tempsList = new ArrayList<TemperatureItem>();

    public void updateDataFromLocalDB(Context context, RecyclerView recyclerView) {

        class GetTempsFromLocalDBTask extends AsyncTask<Object, Object, Void> {
            private Context context;
            private RecyclerView recyclerView;

            public GetTempsFromLocalDBTask(Context context, RecyclerView recyclerView) {
                this.context = context;
                this.recyclerView = recyclerView;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                long start = System.nanoTime();
            }

            @Override
            protected Void doInBackground(Object... params) {
                long start;
                final TemperaturesDBController databaseController = new TemperaturesDBController(context);
                // get the last date recorded on the local DB
                start = System.nanoTime();
                Date date = databaseController.getLatestTempRecordDate();
                LLog.d(TAG, "Got the last record date in local DB within :" + MyHelper.convertTime(System.nanoTime() - start));
                // get the latest data from the DB. Removing the time will get the temps since 12.00AM of that day
                if (date == null) date = new Date("1/1/1900");
                else date = DateUtils.removeTime(date);

                start = System.nanoTime();
                List<TemperatureItem> latestTemperaturesList = databaseController.getAllTemperatureItemsListAfterDate(date);
                LLog.d(TAG, "Loaded " + latestTemperaturesList.size() + " values from local DB in " + MyHelper.convertTime(System.nanoTime() - start));
                tempsList.clear();

                start = System.nanoTime();
                tempsList.addAll(latestTemperaturesList);
                LLog.d(TAG, "Loaded TempList from local DB in " + MyHelper.convertTime(System.nanoTime() - start));
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                long start = System.nanoTime();
                ((RecyclerAdapterWithSetValues) recyclerView.getAdapter()).setValues(tempsList);

//                new StatisticsDBController(context).calculateStats();   // TODO testing. If data from server are loaded, then the stats will be calculated
//                Cursor cur = new StatisticsDBController(context).calculateStats();   // TODO testing. If data from server are loaded, then the stats will be calculated
//                cur.moveToFirst();
//                LLog.d(TAG, "stats records found " + cur.getCount());
//                LLog.d(TAG, "in postExecute. Time to load the RecyclerView : " + MyHelper.convertTime(System.nanoTime() - start));// TODO
            }
        }       // end of nested class


        GetTempsFromLocalDBTask myLocalDBDataTask = new GetTempsFromLocalDBTask(context, recyclerView);
        LLog.d(TAG, "Preparing task getLocalData");   // TODO
        myLocalDBDataTask.execute();    // create new async task and execute it
    }

}
