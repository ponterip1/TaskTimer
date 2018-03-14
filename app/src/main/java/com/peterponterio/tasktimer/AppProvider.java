package com.peterponterio.tasktimer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by peterponterio on 3/11/18.
 *
 * Provider for the TaskTimer app. This is the only class that knows about {@link AppDatabase}
 *
 * All access to the data, whether its reading or writing to it will be done with this content
 * provider class
 */


public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";

    private AppDatabase mOpenHelper;


    private static final UriMatcher sUriMatcher = buildUriMatcher();



    /*
        a content uri is a URI that identifies data in a provider. Content URI's include the symbolic name
        of the entire provider(its authority) and a name that points to a table or file(a path)

        a provider usually has a single authority

        when other classes as well as other apps use this content provider, they will use the URI. So
        the CONTENT_AUTHORITY_URI is public so that it can be used outside of the app
     */
    static final String CONTENT_AUTHORITY = "com.peterponterio.tasktimer.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);



    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;

    private static final int TIMINGS = 200;
    private static final int TIMINGS_ID = 201;


    /*
        private static final int TASK_TIMINGS = 300;
        private static final int TASK_TIMINGS_ID = 301;
     */

    private static final int TASK_DURATIONS = 400;
    private static final int TASK_DURATIONS_ID = 401;



    /*
        creates a new Uri matcher object

        add Uri's that we want to match
        returns values(ex: 100,101, 200,201, 400,401)
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //  content://com.peterponterio.tasktimer.provider/Tasks
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASKS);
        //  content://com.peterponterio.tasktimer.provider/Tasks/8
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME + "/#", TASKS_ID);


//        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS);
//        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME + "/#", TIMINGS_ID);
//
//
//        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME, TASK_DURATIONS);
//        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME + "/#", TASK_DURATIONS_ID);

        return matcher;
    }










    @Override
    public boolean onCreate() {
        //get the instance of the database and store it in mOpenHelper.
        mOpenHelper = AppDatabase.getInstance(getContext());

        return true;
    }










    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: called with URI " + uri);
        //use the value of match to decide which uri was passed into the query method
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();


        /*
            using the switch statement to choose different blocks of code depending on the results of
            matching the uri

            when the uri matches TASKS, TIMINGS, or TASK_DURATIONS, we call the set tables method to
            tell queryBuilder which table we want to query from

            when an id is included, the uri matches TASKS_ID, TIMINGS_ID, or TASK_DURATIONS_ID, we call
            the getTaskId, getTimingId, or getDuration method and that will extract the ID from the uri.
            Then we call queryBuilder's .appendWhere method and that adds a WHERE clause to the query


            its up to the contract classes(TasksContract, TimingsContract, DurationsContract) to parse
            information out of the uri
            there is no need for the content provider to know that the id appears in the second segment
            of the path, it just needs to get the id from the uri
         */
        switch(match) {
            case TASKS:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                break;

            case TASKS_ID:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                long taskId = TasksContract.getTaskId(uri);
                queryBuilder.appendWhere(TasksContract.Columns._ID + " = " + taskId);
                break;




//            case TIMINGS:
//                queryBuilder.setTables(TimingsContract.TABLE_NAME);
//                break;

//            case TIMINGS_ID:
//                queryBuilder.setTables(TimingsContract.TABLE_NAME);
//                long timingId = TimingsContract.getTimingId(uri);
//                queryBuilder.appendWhere(TimingsContract.Columns._ID + " = " + timingId);
//                break;
//


//
//            case TASK_DURATIONS:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                break;

//            case TASK_DURATIONS_ID:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                long durationId = DurationsContract.getDuration(uri);
//                queryBuilder.appendWhere(DurationsContract.Columns._ID + " = " + durationId);
//                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        //return readable database object
        //were only querying the database so we dont need to call getWriteableDatabase
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }







    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        //returns appropriate MIME type
        switch (match) {
            case TASKS:
                return TasksContract.CONTENT_TYPE;

            case TASKS_ID:
                return TasksContract.CONTENT_ITEM_TYPE;

//            case TIMINGS:
//                return TimingsContract.Timings.CONTENT_TYPE;
//
//            case TIMINGS_ID:
//                return TimingsContract.Timings.CONTENT_ITEM_TYPE;
//
//            case TASK_DURATIONS:
//                return DurationsContract.TaskDurations.CONTENT_TYPE;
//
//            case TASK_DURATIONS_ID:
//                return DurationsContract.TaskDurations.CONTENT_ITEM_TYPE;
//
//
            default:
                throw new IllegalArgumentException("unknown Uri: " + uri);
        }
    }










    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //check the uri and get a result back from the uri matcher
        Log.d(TAG, "Entering insert, called with uri: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;

        Uri returnUri;
        long recordId;


        switch (match) {
            case TASKS:
                /*
                    the insertion is done by db's insert method and we just give it the name of the table
                    to insert into, and also a list of values to insert. So whatever the ContentValues
                    object is, we're just passing that onto the database insert method. And because we're
                    just passing it onto the database insert method, we dont need to call any of it methods

                    the db insert method will return the ID of the new row
                 */
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(TasksContract.TABLE_NAME, null, values);
                if(recordId >=0) {
                    //appending the id to the uri
                    returnUri = TasksContract.buildTaskUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                recordId = db.insert(TimingsContract.Timings.buildTimingUri(recordId));
//                if(recordId >=0) {
//                    returnUri = TimingsContract.Timings.buildTimingUri(recordId);
//                } else {
//                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
//                }
//                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        Log.d(TAG, "Exiting insert, returning " + returnUri);
        return returnUri;
    }


















    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "delete called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is  " + match);

        final SQLiteDatabase db;
        int count;
        String selectionCriteria;


        switch (match) {

            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(TasksContract.TABLE_NAME, selection, selectionArgs);
                break;


            case TASKS_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = TasksContract.getTaskId(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskId;

                if((selection != null) && (selection.length()>0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;



//            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.delete(TimingsContract.TABLE_NAME, selection, selectionArgs);
//                break;
//
//            case TIMINGS_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long timingsId = TimingsContract.getTaskId(uri);
//                selectionCriteria = TimingsContract.Columns._ID + " = " + timingsId;
//
//                if((selection != null) && (selection.length()>0)) {
//                    selectionCriteria += " AND (" + selection + ")";
//                }
//                count = db.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs);
//                break;




            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        Log.d(TAG, "Exiting update, returning " + count);

        return count;
    }













    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is  " + match);

        final SQLiteDatabase db;
        int count;
        String selectionCriteria;


        switch (match) {
            /*
            we get a database onject and we call its update method to perform the update. we have to
            specify the table name, the values to update, and the selection criteria that will be used in
            a SQL WHERE clause. Then we just pass on the values that are provided when our update method
            is called. So the databases update method will return a count of the number of rows that
            are updated

            performing an update against the whole table
         */
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(TasksContract.TABLE_NAME, values, selection, selectionArgs);
                break;


            /*
                start by getting the id from the uri using the getTaskId method and then we have to
                build up a string containing the condition for the WHERE clause

                check to see if the selection parameter contains something and if it does, we add it
                to the selection criteria
             */
            case TASKS_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = TasksContract.getTaskId(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskId;

                if((selection != null) && (selection.length()>0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(TasksContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;



//            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.update(TimingsContract.TABLE_NAME, values, selection, selectionArgs);
//                break;
//
//            case TIMINGS_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long timingsId = TimingsContract.getTaskId(uri);
//                selectionCriteria = TimingsContract.Columns._ID + " = " + timingsId;
//
//                if((selection != null) && (selection.length()>0)) {
//                    selectionCriteria += " AND (" + selection + ")";
//                }
//                count = db.update(TimingsContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
//                break;




            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        Log.d(TAG, "Exiting update, returning " + count);

        return count;
    }


}
