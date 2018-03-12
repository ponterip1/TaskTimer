package com.peterponterio.tasktimer;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.peterponterio.tasktimer.AppProvider.CONTENT_AUTHORITY;
import static com.peterponterio.tasktimer.AppProvider.CONTENT_AUTHORITY_URI;

/**
 * Created by peterponterio on 3/8/18.
 */

public class TasksContract {

    static final String TABLE_NAME = "tasks";

    //Tasks fields
    /*
        static class cant be referred to as Columns, it has to be referred to as TasKContract.Columns

        Advantage of this is we can several columns classes
     */
    public static class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String TASKS_NAME = "Name";
        public static final String TASKS_DESCRIPTION = "Description";
        public static final String TASKS_SORTORDER = "SortOrder";


        private Columns() {
            //private constructor to prevent instantiation
        }

    }



    /*
        The URI to access the Tasks table

        CONTENT_URI can now be used by external classes to refer to our tasks table
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);




    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;



    //add an id to the uri(CONTENT_URI)
    static Uri buildTaskUri(long taskId) {
        return ContentUris.withAppendedId(CONTENT_URI, taskId);
    }



    //used to exact(return) the id from a uri
    static long getTaskId(Uri uri) {
        return ContentUris.parseId(uri);
    }









}
