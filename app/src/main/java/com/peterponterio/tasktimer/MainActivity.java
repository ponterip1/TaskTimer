package com.peterponterio.tasktimer;


import android.annotation.SuppressLint;
import android.support.v7.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,
        AddEditActivityFragment.OnSaveClicked, AppDialog.DialogEvents {
    private static final String TAG = "MainActivity";


    //whether or not the activity is in 2-pane mode
    //ex: running in landscape mode on a tablet
    private boolean mTwoPane = false;



    public static final int DIALOG_ID_DELETE = 1;
    public static final int DIALOG_ID_CANCEL_EDIT = 2;


    /*
        module scope because we need to dismiss it in onStop method, when orientation
        changes, to avoid memory leaks
     */
    private AlertDialog mDialog = null;





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
                showAboutDialog();
                break;
            case R.id.menumain_generate:
                break;
        }

        return super.onOptionsItemSelected(item);
    }








    @SuppressLint("SetTextI18n") //suppress warnings when you're absolutely sure you don't need them
    public void showAboutDialog() {
        /*
            when creating a custom dialog, you have to inflate the XML. That creates the view that the
            dialog is gonna be displaying. Dialogs arent part of the underlying activity, the display
            on top of it and are completely separate. So theres no rootView the we can use, which is why
            the root parameter in the .inflate method is set to null

            Next we're creating an AlertDialog.Builder object, and thats
            going to do all the work of building the dialog for us.

            Then we set the title and icon for the dialog, calling the builder.setTitle and builder.setIcon
            methods.

            The setView method uses our InflatedView (messageView) as the dialogs contents. After that
            we call the builder.create method to create the dialog, and we store a reference to it in
            our mDialog field.

            Dialogs normally cancel when you type away from them on the screen, thats the default behavior.
            If you want to prevent the dialog from being cancelled when the user types away from it, paA
            the setCanceledOnTouchOutside() method a value of false.

            Then we get a reference to the about_version TextView so that we can display the version
            information. Then finally, we're showing the dialog with the show() method
         */
        @SuppressLint("InflateParams") View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(messageView);



        //onClick listener set to button inside dialog, clicking button dismisses it
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });


        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);



//        //onClick listener set to dialog View, clicking dialog dismisses it. (not good to use when there are links in dialog. ex: email/web)
//        messageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mDialog != null && mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//            }
//        });





        TextView tv = (TextView) messageView.findViewById(R.id.about_version);
        tv.setText("v" + BuildConfig.VERSION_NAME); //no need to use a string resource for one letter









        /*
            onclick listener set on TextView for URL in "about" page. Used for lower API's that cant
            open up websites that dont end in .com/.org/ etc

            when textview is tapped, we're creating a new intent. we're using the Intent.ACTION_VIEW
            as the action. Then we get the text from the textView and converting it into a string and then
            we're adding that to the intent.setData method. basically formulating the URL

            THIS WILL ONLY BE CALLED ON API's UNDER 20 AND BELOW
         */
        TextView about_url = (TextView) messageView.findViewById(R.id.about_url);
        if(about_url != null) {
            about_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String s = ((TextView) view).getText().toString();
                    intent.setData(Uri.parse(s));

                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, "No browser application found, cannot visit world-wide web", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }











        mDialog.show();
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
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, task.getId(), task.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

        //puts task id in bundle
        args.putLong("TaskId", task.getId());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
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
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");

        switch (dialogId) {
            case DIALOG_ID_DELETE: //user clicks yes to confirm delete task
                //retrieving taskId from bundle
                Long taskId = args.getLong("TaskId");
                if(BuildConfig.DEBUG && taskId ==0) throw new AssertionError("Task ID is zero");
                getContentResolver().delete(TasksContract.buildTaskUri(taskId), null, null);
                break;
            case DIALOG_ID_CANCEL_EDIT:
                //user clicks continue editing when back button is pressed while editing task (doesnt abandon the edit)
                //no action required
                break;
        }
    }



    //interface method (AppDialog)
    @Override
    public void onNegativeDialogResults(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResults: called");

        switch(dialogId) {
            case DIALOG_ID_DELETE: //user clicks no to cancel delete task
                //no action required
                break;
            case DIALOG_ID_CANCEL_EDIT:
                //user clicks abandon changes when back button is pressed while editing task
                finish(); //finishes the activity
                break;
        }

    }




    //interface method (AppDialog)
    @Override
    public void onDialogCancelled(int dialogID) {
        Log.d(TAG, "onDialogCancelled: called");

    }
    
    
    
    


    //when androids back button (white triangle button at bottom) is pressed (not the one at the top of the app)
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");

        /*
            to find out if we're showing the fragment, we use a call to findFragmentById and we're getting
            a reference to the fragment. If thats null, then that means we're not displaying the fragment
            and therefore we're not in two pane mode, which means we're just showing a list of tasks on the
            screen and we can safely allow the user to close down the app. we dont need to pop up a notification.
            So if theres no fragment or the fragment says we can close it, then we call the super.onBackPress(),
            which is the normal code to close down the app

            if we are showing the fragment(edit screen) we ask the user for confirmation with a dialog
         */
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.task_details_container);
        if((fragment == null) || fragment.canClose()) {
            super.onBackPressed();
        } else {
            //show dialog to get confirmation to quit editing
            AppDialog dialog = new AppDialog();
            Bundle args = new Bundle();
            args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT);
            args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
            args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
            args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);

            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), null);
        }
    }







    /*
        dismisses the dialog. This is why mDialog had to be a field, we needed a reference to the dialog
        so we could dismiss it in onStop
     */
    @Override
    protected void onStop() {
        super.onStop();
        if(mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
