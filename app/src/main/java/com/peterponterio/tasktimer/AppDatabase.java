package com.peterponterio.tasktimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by peterponterio on 3/8/18.
 *
 * Basic database class for the application
 *
 * the only class that should use this is AppProvider
 */

class AppDatabase extends SQLiteOpenHelper {

    private static final String TAG = "AppDatabase";

    public static final String DATABASE_NAME = "TaskTimer.db";
    public static final int DATABASE_VERSION = 1;

    //implement AppDatabase as a Singleton
    private static AppDatabase instance = null;


    /*
        constructor calls the super class constructor and passes it the context,
        the database name, and the database version. The third argument that is null is used if you
        want to provide your own cursor factory to create your cursor objects

        constructor is private because we only want a single instance of this class to exist. As well as
        create the database, this class also provides the connection to the database

        making the constructor private is the first step to making a singleton class(class that only has
        one instance)
     */
    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: constructor");
    }


    /**
     *
     * Get an instance of the app's singleton database helper object
     *
     * whenever another class needs an instance of this AppDatabase class, it actually calls the static
     * method getInstance. Ensuring we only have one copy of the instance field that has been set up at
     * any point in time
     *
     * @param context the content providers context
     * @return a SQLite database helper object
     */
    static AppDatabase getInstance(Context context) {
        if(instance == null) {
            Log.d(TAG, "getInstance: creating new instance");
            instance = new AppDatabase(context);
        }

        return instance;

    }


    //called when database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");

        String sSQL; //use a string variable to facilitate logging

//        sSQL = "CREATE TABLE tasks (_id INTEGER PRIMARY KEY NOT NULL, Name TEXT NOT NULL, Description TEXT, SortOrder INTEGER);";
        /*
            replaced all the hardcoded references with the constants in the taskContract list
         */
        sSQL = "CREATE TABLE " + TaskContract.TABLE_NAME + " ("
                + TaskContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TaskContract.Columns.TASKS_NAME + " TEXT NOT NULL, "
                + TaskContract.Columns.TASKS_DESCRIPTION + " TEXT, "
                + TaskContract.Columns.TASKS_SORTORDER + " INTEGER);";

        Log.d(TAG, sSQL);
        db.execSQL(sSQL); //executes the sql passed in by sSQL

        Log.d(TAG, "onCreate: ends");

    }


    //only on version 1 so theres nothing to upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");

        switch(oldVersion) {
            case 1:
                //upgrade logic from version 1
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion: " + newVersion);
        }
        Log.d(TAG, "onUpgrade: ends");

    }
}
