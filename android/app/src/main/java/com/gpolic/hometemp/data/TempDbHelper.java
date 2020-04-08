package com.gpolic.hometemp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gpolic.hometemp.data.TempDbContract.TempEntry;

public class TempDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "hometemptest";

    private static TempDbHelper mInstance = null;

    public TempDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TEMP_TABLE = "CREATE TABLE " + TempEntry.TABLE_NAME + " (" +
                TempEntry._ID + " timestamp PRIMARY KEY NOT NULL, " +
                TempEntry.COLUMN_TEMPERATURE + " FLOAT NOT NULL, " +
                TempEntry.COLUMN_HUMIDITY + " FLOAT NOT NULL" +
                ");";

        final String SQL_CREATE_TEMP_INDEX = "CREATE UNIQUE INDEX timest_idx ON " + TempEntry.TABLE_NAME + " (" + TempEntry._ID + ");";

        final String SQL_CREATE_STATS_TABLE = "create table stats (datest date PRIMARY KEY NOT NULL, max float NOT NULL, min float NOT NULL, avg float NOT NULL);";
//        final String SQL_CREATE_STATS_INDEX = "CREATE UNIQUE INDEX datest_idx ON " + StatisticsEntry.TABLE_NAME + " (" + StatisticsEntry._ID + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TEMP_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TEMP_INDEX);
        sqLiteDatabase.execSQL(SQL_CREATE_STATS_TABLE);
//        sqLiteDatabase.execSQL(SQL_CREATE_STATS_INDEX);
    }


    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // drop older table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TempEntry.TABLE_NAME);

        // create new table
        onCreate(sqLiteDatabase);
    }

    public static TempDbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TempDbHelper(context.getApplicationContext());
        }
        return mInstance;
    }

}