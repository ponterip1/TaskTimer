package com.peterponterio.tasktimer;

import android.provider.BaseColumns;

/**
 * Created by peterponterio on 3/8/18.
 */

public class TaskContract {

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

}
