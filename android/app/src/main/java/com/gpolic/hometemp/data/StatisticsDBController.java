package com.gpolic.hometemp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gpolic.hometemp.logger.LLog;
import com.gpolic.hometemp.model.TempStatItem;
import com.gpolic.hometemp.util.MyHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.gpolic.hometemp.util.DateUtils.stringToDate;

/**
 * helper for the statistics table
 * <p>
 * create table stats (datest date PRIMARY KEY NOT NULL, max float NOT NULL, min float NOT NULL, avg float NOT NULL);
 * insert into stats SELECT DATE(t.timest), MAX(t.temp), MIN(t.temp) , ROUND(AVG(t.temp),2) FROM tempLog t GROUP BY DATE(t.timest);
 * <p>
 * <p>
 * SELECT DATE(t.timest) AS timest, MAX(t.temp) as  max, MIN(t.temp) as min, ROUND(AVG(t.temp),2) as avg  FROM tempLog t GROUP BY DATE(t.timest)
 */
public class StatisticsDBController {

    // Database fields
    private TempDbHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    private Date lastStatsCalculated;

    public StatisticsDBController(Context context) {
        dbHelper = TempDbHelper.getInstance(context);
    }

    public void calculateStats() {

        // TODO check when the stats were calculated, and dont calc them all the time !
        if (lastStatsCalculated != null) {
            Date now = new Date();
        } else {

        }

        long start;
        start = System.nanoTime();
        database = dbHelper.getWritableDatabase();
        database.beginTransaction();

        database.execSQL("DELETE FROM " + TempDbContract.StatisticsEntry.TABLE_NAME);

        String generateStatisticsSQL = "INSERT INTO " + TempDbContract.StatisticsEntry.TABLE_NAME +
                " SELECT DATE(t." + TempDbContract.TempEntry._ID +
                "), MAX(t." + TempDbContract.TempEntry.COLUMN_TEMPERATURE +
                "), MIN(t." + TempDbContract.TempEntry.COLUMN_TEMPERATURE +
                "), ROUND(AVG(t." + TempDbContract.TempEntry.COLUMN_TEMPERATURE + "),2) FROM " +
                TempDbContract.TempEntry.TABLE_NAME + " t " +
                "GROUP BY DATE(t." + TempDbContract.TempEntry._ID + ");";

        database.execSQL(generateStatisticsSQL);
        database.setTransactionSuccessful();
        database.endTransaction();

        database.close();
        lastStatsCalculated = new Date();

        LLog.d("StatDBController", "update stats in local DB in " + MyHelper.convertTime(System.nanoTime() - start));
    }


    public List<TempStatItem> getAllTempStatisticsList() {
        List<TempStatItem> tempStatList = new ArrayList<TempStatItem>();
        // Select All Query, and place the latest records on top
        String selectQuery = "SELECT * FROM " + TempDbContract.StatisticsEntry.TABLE_NAME;
//                + " ORDER BY " + TempDbContract.StatisticsEntry._ID + " DESC";

        Cursor cursor = dbHelper.getWritableDatabase().rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TempStatItem statisticItem = new TempStatItem();

                String test = cursor.getString(0);
                statisticItem.setDate((stringToDate(cursor.getString(0))));
                statisticItem.setMax(Float.valueOf(cursor.getString(1)));
                statisticItem.setMin((Float.valueOf(cursor.getString(2))));
                statisticItem.setAvg((Float.valueOf(cursor.getString(3))));
                // Adding contact to list
                tempStatList.add(statisticItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return tempStatList;
    }


    public void close() {
        dbHelper.close();
    }


}
