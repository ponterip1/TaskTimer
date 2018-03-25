package com.peterponterio.tasktimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by peterponterio on 3/21/18.
 */

public class AppDialog extends DialogFragment {
    private static final String TAG = "AppDialog";


    public static final String DIALOG_ID = "id";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_RID = "positive_rid";
    public static final String DIALOG_NEGATIVE_RID = "negative_rid";




    /*
        The dialog's callback interface to notify of user selected results (deletion confirmed, etc)

        first parameter is the dialogID, second parameter is the Bundle that was passed when the
         dialog fragment is created

         Our dialog fragment is a fragment, so our fragments constructor cant have any parameters. If
          we want to pass any values into the dialog fragment, we use a bundle. The values are stored
          in a bundle as a key-value pair. In this dialog fragment, we're going to provide more than
          one value
     */
    interface DialogEvents {
        void onPositiveDialogResult(int dialogID, Bundle args); //user clicked yes on dialog
        void onNegativeDialogResults(int dialogID, Bundle args); //user clicked no on dialog
        void onDialogCancelled(int dialogID); //user backed out of dialog
    }







    private DialogEvents mDialogEvents;


    /*
        we need to check that the calling activity implements the required interface by using the
        instanceof in the if statement. If it doesnt, we throw a ClassCastException error
     */
    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: Entering onAttach, activity is " + context.toString());
        super.onAttach(context);

        //Activities containing this fragments must implement its callbacks.
        if(!(context instanceof DialogEvents)) {
            throw new ClassCastException(context.toString() + " must implement AppDialog.DialogEvents interface");
        }

        mDialogEvents = (DialogEvents) context;

    }









    /*
        making sure that we wont attempt a callback method if the activities been destroyed
     */
    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: Entering...");
        super.onDetach();

        //Reset the active callbacks interface, because we dont have an activity any longer
        mDialogEvents = null;
    }
















    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: starts");


        /*
            used an alert dialog, so we created a new alert dialog builder that'll build the dialog.

            This is pretty much boilerplate code

            Each of the alertDialog builder methods returns the instance that it was called on, and we can
            chain together the method calls. The codes using the setMessage method to tell the builder
            what text to display in the setPositive and setNegative methods so it can set the text for
            the buttons and add an onClickListener to them
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        final Bundle arguments = getArguments();
        final int dialogId;
        String messageString;
        int positiveStringId;
        int negativeStringId;


        /*
            extracting the values from the bundle that was provided when the dialog fragment was created

            as long as there is a bundle(null check) we can retrieve the values. The Dialog fragment
            relies on being provided with atleast a message to display(DIALOG_MESSAGE) and the callback interface requires
            a dialog id(DIALOG_ID), so if the arguments are null we throw an IllegalArgumentException.

            if text isnt provided for the two buttons, theyre going to default to showing OK and Cancel.
            We pass string resource Id's instead of strings which is why all the values are ints instead
            of strings (positiveStringId, negativeStringId)
         */
        if (arguments != null) {
            dialogId = arguments.getInt(DIALOG_ID);
            messageString = arguments.getString(DIALOG_MESSAGE);


            //if either value is missing from bundle, throw exception
            if(dialogId == 0 || messageString == null) {
                throw new IllegalArgumentException("DIALOG_ID and/or DIALOG_MESSAGE not present in the bundle");
            }



            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID);

            if(positiveStringId == 0) {
                positiveStringId = R.string.ok;
            }




            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID);

            if(negativeStringId == 0) {
                negativeStringId = R.string.cancel;
            }

        } else {
            throw new IllegalArgumentException("Must pass DIALOG_ID and DIALOG_MESSAGE in the bundle");
        }


        builder.setMessage(messageString)
                .setPositiveButton(positiveStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //callback positive result method
                        //null check
                        if(mDialogEvents != null) {
                            mDialogEvents.onPositiveDialogResult(dialogId, arguments);
                        }
                    }
                })
                .setNegativeButton(negativeStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //callback negative result method
                        //null check
                        if(mDialogEvents != null) {
                            mDialogEvents.onNegativeDialogResults(dialogId, arguments);
                        }
                    }
                });




        return builder.create();
    }






    //called when the user presses the back button, or taps the screen outside the dialog
    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(TAG, "onCancel: called");

        //callback the listener using its onDialog.Cancelled method
        if(mDialogEvents != null) {
            int dialogId = getArguments().getInt(DIALOG_ID);
            mDialogEvents.onDialogCancelled(dialogId);
        }
    }
}




























