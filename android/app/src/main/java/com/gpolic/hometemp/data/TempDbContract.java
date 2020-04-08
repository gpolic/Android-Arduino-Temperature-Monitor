package com.gpolic.hometemp.data;

import android.content.ContentResolver;
import android.provider.BaseColumns;

class TempDbContract {
    public static final String CONTENT_AUTHORITY = "com.gpolic.hometemp";

    public static final class TempEntry implements BaseColumns {

        public static final String TABLE_NAME = "tempLog";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY;

        public static final String _ID = "timest";
        public static final String COLUMN_TEMPERATURE = "temp";
        public static final String COLUMN_HUMIDITY = "humidity";
    }

    public static final class StatisticsEntry implements BaseColumns {
        public static final String TABLE_NAME = "stats";

        public static final String _ID = "datest";
        public static final String COLUMN_MAX = "max";
        public static final String COLUMN_MIN = "min";
        public static final String COLUMN_AVG = "avg";
    }
}