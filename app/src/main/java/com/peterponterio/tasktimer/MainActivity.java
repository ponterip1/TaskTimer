package com.peterponterio.tasktimer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,
        AddEditActivityFragment.OnSaveClicked, AppDialog.DialogEvents {
    private static final String TAG = "MainActivity";


    //whether or not the activity is in 2-pane mode
    //ex: running in landscape mode on a tablet
    private boolean mTwoPane = false;


    private static final String ADD_EDIT_FRAGMENT = "AddEditFragment";

    public static final int DELETE_DIALOG_ID = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        /*
            Check to see if the layout contains a view with the ID task_details_container and set mTwoPane
            to true if it does, otherwise mTwoPane stays false, which is how we initialized it
        */
        if(findViewById(R.id.task_details_container) != null) {
            //The detail container view will be present only in the large screen layouts (res/values-land and res/values-sw600dp).
            //If this view is present, then the activity should be in two-pane mode
            mTwoPane = true;
        }
    }












    /*
        interface method (AddEditActivityFragment)

        get the activity to implement the interface and respond appropriately when their onSaveClicked method
        is called
    */
    @Override
    public void onSaveClicked() {
        /*
            to remove the fragment we have to pass a fragment object to the remove method

            mainActivity removes the fragment when the save button is tapped(closes the edit view)
         */
        Log.d(TAG, "onSaveClicked: starts");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container); //finds fragment

        /*
            if we find the fragment, we use a fragmentManager to being a fragmentTransaction. We then
            remove the fragment abd we commit the change when we're done
         */
        if(fragment != null) {
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.remove(fragment);
//            fragmentTransaction.commit();
            //same code as above (chaining methods together)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
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

        //check which menu item is tapped
        switch (id) {
            case R.id.menumain_addTask:
                taskEditRequest(null);
                break;
            case R.id.menumain_showDurations:
                break;
            case R.id.menumain_settings:
                break;
            case R.id.menumain_showAbout:
                break;
            case R.id.menumain_generate:
                break;
        }

        return super.onOptionsItemSelected(item);
    }





    //interface methods (CursorRecyclerViewAdapter)
    @Override
    public void onEditClick(Task task) {
        taskEditRequest(task);
    }


    //interface methods (CursorRecyclerViewAdapter)
    @Override
    public void onDeleteClick(Task task) {
        Log.d(TAG, "onDeleteClick: starts");


        /*
            creating a new AppDialog and added our DIALOG_ID and DELETE_DIALOG_ID as well as the caption
            in the DIALOG_POSITIVE_RID key to the bundle.

            shows dialog to request confirmation before deleting a task
         */
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, task.getId(), task.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

        //puts task id in bundle
        args.putLong("TaskId", task.getId());

        dialog.setArguments(args);
        dialog.show(getFragmentManager(), null);
    }














    /*
        start of by seeing if the app should be in two pane mode. If the apps running on a phone in
        portrait mode, we start the AddEditActivity using an intent. If our taskEditRequest method's
        called with a task argument that isnt null, then that indicates that an existing task is to be
        edited. but if the task is null, then we're adding a new task. So if we have a task to edit,
        we add it to the intent by calling the putExtra method

        if the app is in two-pane mode, we create a fragmentManager and we call the beginTransaction
        method. That gives us a fragment transaction object that we used to perform the operations that
        we want on our fragments. Everything is done through a fragment transaction incase you want to
        add more than one fragment at the same time or remove some fragments and replace them with others.
        A fragment transaction queues up all of our changes and then performs them once we call commit.
        That way, everything can appear smooth and the user shouldnt see gaps appearing when one fragment
        is removed and the new one is added. We use the fragment transaction to add our fragment and then
        we commit
    */
    private void taskEditRequest(Task task) {
        Log.d(TAG, "taskEditRequest: starts");

        if (mTwoPane) {
            Log.d(TAG, "taskEditRequest: in two-pane mode (tablet");

            AddEditActivityFragment fragment = new AddEditActivityFragment();


            /*
                We pass our task object to the fragment by using a bundle. Instead of using the putExtra
                method, we just create a new bundle and put the task in there.
                The fragment has a setArguments method that we use to add the bundle to it
             */
            Bundle arguments = new Bundle();
            arguments.putSerializable(Task.class.getSimpleName(), task);
            fragment.setArguments(arguments); //added task to bundle and added bundle to fragments arguments


            //v4 import so older API's can use
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.task_details_container, fragment);
//            fragmentTransaction.commit();
            // same code as above (chaining methods together)
            getSupportFragmentManager().beginTransaction().replace(R.id.task_details_container, fragment).commit();

        } else {
            Log.d(TAG, "taskEditRequest: in single-pane mode (phone");

            //in single pane mode, start the detaul activity for the selected item Id
            Intent detailIntent = new Intent(this, AddEditActivity.class);
            if (task != null) { //editing a task
                detailIntent.putExtra(Task.class.getSimpleName(), task);
                startActivity(detailIntent);
            } else { //adding a new task
                startActivity(detailIntent);
            }
        }


    }









    //interface method (AppDialog)
    @Override
    public void onPositiveDialogResult(int dialogID, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");

        //retrieving taskId from bundle
        Long taskId = args.getLong("TaskId");

        getContentResolver().delete(TasksContract.buildTaskUri(taskId), null, null);
    }



    //interface method (AppDialog)
    @Override
    public void onNegativeDialogResults(int dialogID, Bundle args) {
        Log.d(TAG, "onNegativeDialogResults: called");

    }




    //interface method (AppDialog)
    @Override
    public void onDialogCancelled(int dialogID) {
        Log.d(TAG, "onDialogCancelled: called");

    }
}
