package com.peterponterio.tasktimer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
             specify the columns we want to see in the query by creating a string array listing them

             we then get a reference to the devices content resolver and call its query method and because
             were using the uri for our tasks table, the content resolver knows that it has to extract
             the authority from the uri to work out which provider to use and then it passes the entire
             uri onto our provider to run the query

             get a cursor from the content resolver and once weve got it were going to loop through it
            logging all the columns in the cursor, as long as the cursor isnt null
         */
        String[] projection = { TasksContract.Columns._ID,
                                TasksContract.Columns.TASKS_NAME,
                                TasksContract.Columns.TASKS_DESCRIPTION,
                                TasksContract.Columns.TASKS_SORTORDER};
        ContentResolver contentResolver = getContentResolver();


        ContentValues values = new ContentValues();

        /*
            Inserting

            The ContentValues object is used to provide the new values that we're going to insert into
             the database. A ContentValues object is very similar to a bundle. We're using its put method
             to store key value pairs where the key is the column name in the table that we're inserting
             into. We have to specify a value for the name column because it was set as not null when
             we created the tasks table
         */
//        values.put(TasksContract.Columns.TASKS_NAME, "New Task 1");
//        values.put(TasksContract.Columns.TASKS_DESCRIPTION, "Description 1");
//        values.put(TasksContract.Columns.TASKS_SORTORDER, 2);
//        Uri uri = contentResolver.insert(TasksContract.CONTENT_URI, values);


        /*
            Updating

            the update method needs a Uri with a set of values. We're passing the values, and we've
             also included the Uri that includes the id by calling the buildTaskUri method, passing the
             id = 4 in this case, for the record that we want to be updated, and null for the other
             two parameters
         */
//        values.put(TasksContract.Columns.TASKS_NAME, "Content Provider");
//        values.put(TasksContract.Columns.TASKS_DESCRIPTION, "Record content provider video");
//        int count = contentResolver.update(TasksContract.buildTaskUri(4), values, null, null);
//        Log.d(TAG, "onCreate: " + count + " record(s) updated");




        /*
            Updating using selection

            looking for all values in the database with a tasks sort order of 2
            The WHERE clause is the selection and the selectionArgs is null. So weve added a WHERE
            clause to update all entries in the database that have got columns.Task_SortOrder with a
            current value of two
         */
//        values.put(TasksContract.Columns.TASKS_SORTORDER, "99");
//        values.put(TasksContract.Columns.TASKS_DESCRIPTION, "Completed");
//        String selection = TasksContract.Columns.TASKS_SORTORDER + " = " + 2;
//        int count = contentResolver.update(TasksContract.CONTENT_URI, values, selection, null);
//        Log.d(TAG, "onCreate: " + count + " record(s) updated");



        /*
            Updating using the selectionArgs.

            we're providing a single arg value, 99. When the sql statements built up to perform the
            update, each of the values in args is used to replace the question marks in the selection.
            Theyre replaced in order. For this example theres only 1, which is 99, but if you had three
            or four, they would be replaced in order that they appear in the array

            If all the values for the criteria come from inside your code, then it doesnt really matter
            which method to use
         */
//        values.put(TasksContract.Columns.TASKS_DESCRIPTION, "For deletion");
//        String selection = TasksContract.Columns.TASKS_SORTORDER + " = ?";
//        String[] args = {"99"};
//        int count = contentResolver.update(TasksContract.CONTENT_URI, values, selection, args);
//        Log.d(TAG, "onCreate: " + count + " record(s) updated");



        /*
            Deleting

            pass the value of 3, which is the record id we want to delete, no where clause, no selectionArgs
         */
//        int count = contentResolver.delete(TasksContract.buildTaskUri(3), null, null);
//        Log.d(TAG, "onCreate: " + count + " record(s) deleted");



        /*
            Deleting using sectionArgs

            passing taskcontract.CONTENT_URI. The argument we're looking for are TASKS_DESCRIPTIONS
            where the value is set to "For deletion"
         */
//        String selection = TasksContract.Columns.TASKS_DESCRIPTION + " = ?";
//        String[] args = {"For deletion"};
//        int count = contentResolver.delete(TasksContract.CONTENT_URI, selection, args);
//        Log.d(TAG, "onCreate: " + count + " record(s) deleted");






        Cursor cursor = contentResolver.query(TasksContract.CONTENT_URI,
                projection,
                null,
                null,
                TasksContract.Columns.TASKS_SORTORDER);

        /*
            instead of specifying which columns to log, the code just loops through all the columns in
            the cursor and logs the name and value of each one, as well as getting the value of a column
            using the getString method. we can also get the name of the column using the getColumnName
            method
         */
        if(cursor != null){
            Log.d(TAG, "onCreate: number of rows: " + cursor.getCount());
            while (cursor.moveToNext()) {
                for(int i=0; i<cursor.getColumnCount(); i++) {
                    Log.d(TAG, "onCreate: " + cursor.getColumnName(i) + ": " + cursor.getString(i));
                }
                Log.d(TAG, "onCreate: ==================================");
            }
            cursor.close();
        }

//        AppDatabase appDatabase = AppDatabase.getInstance(this);
//        final SQLiteDatabase db = appDatabase.getReadableDatabase();
        
        



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menumain_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
