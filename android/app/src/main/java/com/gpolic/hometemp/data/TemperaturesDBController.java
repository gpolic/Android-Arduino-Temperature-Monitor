package com.gpolic.hometemp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gpolic.hometemp.logger.LLog;
import com.gpolic.hometemp.model.TemperatureItem;
import com.gpolic.hometemp.model.TemperaturesList;
import com.gpolic.hometemp.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by George on 18/8/2017.
 * this is a controller class to handle request to the local sqlite Temperatures database
 */
public class TemperaturesDBController {
    private static final String TAG = TemperaturesDBController.class.getSimpleName();
    // Database fields
    private TempDbHelper dbHelper;
    private SQLiteDatabase database;

    public TemperaturesDBController(Context context) {
        dbHelper = TempDbHelper.getInstance(context);
    }

    public void deleteTemperatureRecord(TemperatureItem tempItem) {
        database = dbHelper.getWritableDatabase();
        database.delete(TempDbContract.TempEntry.TABLE_NAME, TempDbContract.TempEntry._ID + " ?", new String[]{String.valueOf(tempItem.getDate())});
    }


    public int getTempRecordsCount() {
        Cursor cursor = dbHelper.getReadableDatabase().query(TempDbContract.TempEntry.TABLE_NAME, new String[]{"COUNT(*)"},
                null, null, null, null, null);

        int rowCount = 0;
        if (cursor != null)
            if (cursor.moveToFirst()) {
                rowCount = cursor.getInt(0);
                // return contact
                cursor.close();
            }
        return rowCount;
    }


    // TODO change the statement to a ExecSQL INSERT INTO xx VALUES() and see if that is faster
//    public void insertTempItems(List<TemperatureItem> temperatureItems) {
    public void insertTempItems(TemperaturesList temperatureItems) {
        if (temperatureItems.size() == 0) return;

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // use transaction to insert many values faster
//        database.execSQL("BEGIN TRANSACTION");
        database.beginTransaction();

        for (TemperatureItem temperatureItem : temperatureItems.items()) {
            ContentValues values = new ContentValues();
            values.put(TempDbContract.TempEntry._ID, temperatureItem.getDateDBString());
            values.put(TempDbContract.TempEntry.COLUMN_TEMPERATURE, temperatureItem.getTemperature()); // temperature
            values.put(TempDbContract.TempEntry.COLUMN_HUMIDITY, temperatureItem.getHumidity()); // humidity

            // Inserting Row
            database.insert(TempDbContract.TempEntry.TABLE_NAME, null, values);
        }
//        database.execSQL("END TRANSACTION");
        database.setTransactionSuccessful();
        database.endTransaction();

        database.close();
    }

    public Date getLatestTempRecordDate() {
        Cursor cursor = dbHelper.getReadableDatabase().query(TempDbContract.TempEntry.TABLE_NAME, new String[]{"MAX(" + TempDbContract.TempEntry._ID + ")"},
                null, null, null, null, null);

        Date lastItemDateTime = null;
        if (cursor != null)
            if (cursor.moveToFirst()) {
                String date = cursor.getString(0);
                if (date != null)
                    lastItemDateTime = DateUtils.stringToDateTime(cursor.getString(0));
                cursor.close();
            }
        // return contact

        return lastItemDateTime;
    }

    public List<TemperatureItem> getAllTemperatureItemsList() {
        List<TemperatureItem> tempList = new ArrayList<TemperatureItem>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TempDbContract.TempEntry.TABLE_NAME;

        Cursor cursor = dbHelper.getWritableDatabase().rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TemperatureItem temperatureItem = new TemperatureItem();

                String test = cursor.getString(0);
                temperatureItem.setDate((cursor.getString(0)));
                temperatureItem.setTemperature(Float.valueOf(cursor.getString(1)));
                temperatureItem.setHumidity((Float.valueOf(cursor.getString(2))));
                // Adding contact to list
                tempList.add(temperatureItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return tempList;
    }


    public Cursor getAllTemperaturesCursorAfterDate(Date date) {
        String queryDate = DateUtils.dateToDBString(date);
        String selectQuery = "SELECT * FROM " + TempDbContract.TempEntry.TABLE_NAME + " WHERE "
                + TempDbContract.TempEntry._ID + " > '" + queryDate + "' ORDER BY " + TempDbContract.TempEntry._ID + " DESC";
        LLog.d(TAG, "Executing query " + selectQuery);

        return dbHelper.getReadableDatabase().rawQuery(selectQuery, null);
    }


    public List<TemperatureItem> getAllTemperatureItemsListAfterDate(Date date) {
        List<TemperatureItem> tempList = new ArrayList<TemperatureItem>();
        Cursor cursor = getAllTemperaturesCursorAfterDate(date);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TemperatureItem temperatureItem = new TemperatureItem();

                temperatureItem.setDate((cursor.getString(0)));
                temperatureItem.setTemperature(Float.valueOf(cursor.getString(1)));
                temperatureItem.setHumidity((Float.valueOf(cursor.getString(2))));
                // Adding contact to list
                tempList.add(temperatureItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tempList;
    }


    public void deleteAllTemperatureRecords() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TempDbContract.TempEntry.TABLE_NAME, null, null);
        db.close();
    }


    public void close() {
        dbHelper.close();
    }


    //TODO Unused methods
    public TemperatureItem getTempRecord(Date date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TempDbContract.TempEntry.TABLE_NAME, new String[]{
                        TempDbContract.TempEntry._ID,
                        TempDbContract.TempEntry.COLUMN_TEMPERATURE,
                        TempDbContract.TempEntry.COLUMN_HUMIDITY},
                TempDbContract.TempEntry._ID + "=?",
                new String[]{DateUtils.dateToDBString(date)},
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            TemperatureItem temperatureItem = new TemperatureItem(cursor.getString(0),
                    cursor.getString(1), cursor.getString(2));
            // return contact
            cursor.close();
            return temperatureItem;

        } else return null;
    }


    public void insertTempItem(TemperatureItem temperatureItem) {
        ContentValues values = new ContentValues();
        values.put(TempDbContract.TempEntry._ID, temperatureItem.getDateDBString());
        values.put(TempDbContract.TempEntry.COLUMN_TEMPERATURE, temperatureItem.getTemperature()); // temperature
        values.put(TempDbContract.TempEntry.COLUMN_HUMIDITY, temperatureItem.getHumidity()); // humidity

        // Inserting Row
        dbHelper.getWritableDatabase().insert(TempDbContract.TempEntry.TABLE_NAME, null, values);
        values = null;
    }
}
