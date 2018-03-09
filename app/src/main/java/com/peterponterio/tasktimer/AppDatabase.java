package com.peterponterio.tasktimer;

import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by peterponterio on 3/8/18.
 *
 * Basic database class for the application
 *
 * the only class that should use this is AppProvider
 */

class AppDatabase extends SQLiteOpenHelper {

    private static final String TAG = "AppDatabase";

    public static final String DATABASE;
}
