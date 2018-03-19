package com.peterponterio.tasktimer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddEditActivityFragment extends Fragment {
    private static final String TAG = "AddEditActivityFragment";


    //enum used to keep track of whether fragments being used to add or edit
    public enum FragmentEditMode { EDIT, ADD }
    private FragmentEditMode mMode; //private field to track fragment


    /*
        Get references to all our widgets so we can attach a listener to the button and get the text
        type into the edit tasks
     */
    private EditText mNameTextView;
    private EditText mDescriptionTextView;
    private EditText mSortOrderTextView;
    private Button mSaveButton;


    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }


    //Fragments onCreateView method returns the view it inflates
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");

        //store the inflated view
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        /*
            used the view object we created from the inflater to extract out the references to the
            various widgets on screen and we're saving those in our fields so that we can reference them
            elsewhere. Then were returning the view
         */
        mNameTextView = (EditText) view.findViewById(R.id.addedit_name);
        mDescriptionTextView = (EditText) view.findViewById(R.id.addedit_description);
        mSortOrderTextView = (EditText) view.findViewById(R.id.addedit_sortorder);
        mSaveButton = (Button) view.findViewById(R.id.addedit_save);







        /*
            Retrieving the bundle from the arguments that were set before the frament was added using
            the setArguments method in MainActivitys taskEditRequest method
         */
        Bundle arguments = getArguments();








        /*
            task is made final because we're going to refer to it in the buttons onClickListener, and
            an inner class can only access final variables of its enclosing class. That means we have
            to initialize it to null, otherwise theres a path through the code that could result in it
            not being initialized, which results in an error
         */
        final Task task;
        if(arguments != null) {
            Log.d(TAG, "onCreateView: retrieving task details");

            task = (Task) arguments.getSerializable(Task.class.getSimpleName());
            if(task != null) {
                Log.d(TAG, "onCreateView: Task details found, editing...");
                mNameTextView.setText(task.getName());
                mDescriptionTextView.setText(task.getDescription());
                mSortOrderTextView.setText(Integer.toString(task.getSortOrder()));
                mMode = FragmentEditMode.EDIT;
            } else {
                //No task, so we must be adding a new task, and not editing an existing one
                mMode = FragmentEditMode.ADD;
            }
        } else {
            task = null;
            Log.d(TAG, "onCreateView: No arguments, adding new record");
            mMode = FragmentEditMode.ADD;
        }


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update the database if at least one field has changed
                //no need to hit the database unless this has happened

                /*
                    converted to an int right at the start to save time, rather than duplicating the
                    conversion code
                 */
                int so;
                if(mSortOrderTextView.length()>0) {
                    so = Integer.parseInt(mSortOrderTextView.getText().toString());
                } else {
                    so = 0;
                }


                /*
                    get a reference to the activity that the fragments attached to first, and then
                    call getContentResolver on that activity reference
                 */
                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();


                /*
                    if the user clicks the save button without making any changes to the data, then
                     theres no point accessing the database to update a record with the same values.
                     this is really important on an android device because we dont want to be updating the
                     database or doing any processing of any significance unless we really need to

                     So in the case of an EDIT, were checking each field and in the case of an ADD, were
                     adding a new record. we just make sure the name field isnt blank as thats the
                     only column that requires a value on the table.(description and sortorder are optional)

                 */
                switch (mMode) {
                    case EDIT:
                        /*
                            checking each editText value against the orignal task object that was passed
                            in the bundle. and if the value has changed, the new value is added to the
                             values ContentValues and once all the fields have been checked and if theres
                             anything in values, the data is saved by calling the update method of the
                             content resolver
                         */
                        if(!mNameTextView.getText().toString().equals(task.getName())) {
                            values.put(TasksContract.Columns.TASKS_NAME, mNameTextView.getText().toString());
                        }
                        if(!mDescriptionTextView.getText().toString().equals(task.getDescription())) {
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION, mDescriptionTextView.getText().toString());
                        }
                        if(so != task.getSortOrder()) {
                            values.put(TasksContract.Columns.TASKS_SORTORDER, so);
                        }
                        if(values.size() != 0) {
                            Log.d(TAG, "onClick: updating task");
                            contentResolver.update(TasksContract.buildTaskUri(task.getId()), values, null, null);
                        }
                        break;
                    case ADD:
                        if(mNameTextView.length()>0) {
                            Log.d(TAG, "onClick: adding new task");
                            values.put(TasksContract.Columns.TASKS_NAME, mNameTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION, mDescriptionTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_SORTORDER, so);
                            contentResolver.insert(TasksContract.CONTENT_URI, values);
                        }
                        break;
                }
                Log.d(TAG, "onClick: Done editing");
            }
        });
        Log.d(TAG, "onCreateView: Exiting...");

        return view;


    }
}
