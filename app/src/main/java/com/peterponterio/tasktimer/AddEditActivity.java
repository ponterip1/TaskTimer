package com.peterponterio.tasktimer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class AddEditActivity extends AppCompatActivity implements AddEditActivityFragment.OnSaveClicked,
                    AppDialog.DialogEvents {
    private static final String TAG = "AddEditActivity";

    public static final int DIALOG_ID_CANCEL_EDIT = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        AddEditActivityFragment fragment = new AddEditActivityFragment();


        /*
            when we pass a task object in the intent, its passed as a bundle. getExtras() is getting
            the extra arguments that were included in the intent that started the activity that the
            fragment is associated with. When mainActivity wants to add a new task, it doesnt provide
            any extras to the intent, so in this case arguments will be null. Even if there are arguments,
            theres no guarantee that they contain a task.
         */
        Bundle arguments = getIntent().getExtras();


        fragment.setArguments(arguments); //added task to bundle and added bundle to fragments arguments


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();

    }






    /*
        handling when the up button( the arrow button <- ) is pressed. Handling the up button is exactly the same as handling
        the back button, we just do it in the onOptionsItemsSelected method instead of onBackPressed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home button pressed");
                /*
                    get the fragment and check if we can close it

                    Called the FindFragmentById directly on the result of getSupportFragmentManager,
                    rather than storing the result in a fragment manager variable
                 */
                AddEditActivityFragment fragment = (AddEditActivityFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment);
                if(fragment.canClose()) {
                    //we dont want to show the dialog, allows android to deal with button
                    return super.onOptionsItemSelected(item);
                } else {
                    showConfirmationDialog();
                    return true; //indicate we are handling this, prevents button from returning user to MainActivity
                }
            default:
                //we dont want to show the dialog, allows android to deal with button
                return super.onOptionsItemSelected(item);
        }

    }















    /*
            implement the onSaveClicked interface from AddEditActivityFragment

            When we edit a task in portrait mode, the addEditActivity was started and that loads the fragment,
            and when the save button is clicked, the fragment calls addEditActivity's onSaveClicked method and
            addEditActivity then finishes
        */
    @Override
    public void onSaveClicked() {
        /*
            when you want an activity to finish, you just call the finish method
         */
        finish();

    }







    //interface method from AppDialog
    //if tapped, the user wants to continue editing, no action required
    @Override
    public void onPositiveDialogResult(int dialogID, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");

    }






    //interface method from AppDialog
    //if negative button is tapped, the user wants to abandon changes
    @Override
    public void onNegativeDialogResults(int dialogID, Bundle args) {
        Log.d(TAG, "onNegativeDialogResults: called");
        finish();

    }







    //interface method from AppDialog
    @Override
    public void onDialogCancelled(int dialogID) {
        Log.d(TAG, "onDialogCancelled: called");

    }




    public void showConfirmationDialog() {
        //show dialog to get confirmation to quit editing
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);

        dialog.setArguments(args);
        dialog.show(getFragmentManager(), null);
    }








    //when androids back button (white triangle at bottom) is pressed (not the one at the top of the app)
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");

        /*
            to find out if we're showing the fragment, we use a call to findFragmentById and we're getting
            a reference to the fragment. So if theres no fragment or the fragment says we can close it, then we call the super.onBackPress(),
            which is the normal code to close down the app

            if we are showing the fragment(edit screen) we ask the user for confirmation with a dialog
         */
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.fragment);
        if(fragment.canClose()) {
            super.onBackPressed();
        } else {
            showConfirmationDialog();
        }
    }
}


