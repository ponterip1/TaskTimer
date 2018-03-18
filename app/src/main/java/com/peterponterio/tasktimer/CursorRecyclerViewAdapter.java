package com.peterponterio.tasktimer;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by peterponterio on 3/16/18.
 */

/*
    this adapter is going to use a cursor as the source of the data to be displayed
 */
class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {
    private static final String TAG = "CursorRecyclerViewAdapt";

    private Cursor mCursor;
    private OnTaskClickListener mListener;






    /*
        used to respond to tasks being clicked in the recyclerView list
     */
    interface OnTaskClickListener {
        void onEditClick(Task task);
        void onDeleteClick(Task task);
    }








    public CursorRecyclerViewAdapter(Cursor cursor, OnTaskClickListener listener) {
        Log.d(TAG, "CursorRecyclerViewAdapter: Constructor called");
        mCursor = cursor;
        mListener = listener;
    }








    /*
        called by the recycler view when it needs a new view to display

        will inflate a view from the task_list_items layout and then return the view
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_items, parent, false);
        return new TaskViewHolder(view);
    }









    



    /*
        called when the recyclerView wants new data to be displayed and is providing an existing view
        to be reused. this is where we put our data into the individual widgets. RecyclerView first
        requests a new view and then sends that new view back so we can put the data into it
     */
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder: starts");



        //if there wasnt any records, any data, or the cursor was null
        if((mCursor == null) || (mCursor.getCount() == 0)) {
            Log.d(TAG, "onBindViewHolder: providing instructions");
            holder.name.setText(R.string.instructions_heading);
            holder.description.setText(R.string.instructions);
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        } else { //if the cursor isnt null, display the data
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldnt move cursor to position " + position);
            }


            /*
                Created the task that has been clicked on effectively. We can use the task object in
                our onClickListener to log the task name

             */
            final Task task = new Task(mCursor.getLong(mCursor.getColumnIndex(TasksContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)),
                    mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_DESCRIPTION)),
                    mCursor.getInt(mCursor.getColumnIndex(TasksContract.Columns.TASKS_SORTORDER)));




            /*
                as long as we've got a valid cursor that contains atleast one row, the code retrieves
                the value from the cursor and uses them to set the text in the textView widgets
             */
            holder.name.setText(task.getName());
            holder.description.setText(task.getDescription());
            holder.editButton.setVisibility(View.VISIBLE); // TODO add onClick Listener
            holder.deleteButton.setVisibility(View.VISIBLE); //TODO add onClick Listener


            /*
                onClickListener will call the appropriate methods in our mListener object when one of
                the buttons is clicked. The button calls back to the cursorRecyclerViewAdapter class
                when the button is tapped and this class will call back the activity or the fragment
                passing it to the task that needs to be edited or deleted. So in that way, the activity
                or fragment can take care of editing a task or deleting it. Editing and Deleting will
                be initiated by MainActivity and not a fragment
             */
            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.d(TAG, "onClick: starts");
                    switch (view.getId()) {
                        case R.id.tli_edit:
                            if(mListener != null) {
                                mListener.onEditClick(task);
                            }
                            break;
                        case R.id.tli_delete:
                            if(mListener != null){
                                mListener.onDeleteClick(task);
                            }
                            break;
                        default:
                            Log.d(TAG, "onClick: found unexpected button id");
                    }

//                    Log.d(TAG, "onClick: button with id " + view.getId() + " clicked");
//                    Log.d(TAG, "onClick: task name is " + task.getName());
                }
            };

            //setting onclick listener to buttons
            holder.editButton.setOnClickListener(buttonListener);
            holder.deleteButton.setOnClickListener(buttonListener);

        }

    }







    /*
        recyclerView uses this so it knows how many items there are to display. If there are no items,
        we will be sending back a view that displays the instructions. So in this case we dont want to
         tell the recyclerView that there are no records. we'll always return atleast 1

     */
    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: starts");

        if((mCursor == null) || (mCursor.getCount() ==0)) {
            return 1; //we return 1 because we populate a single ViewHolder with instructions
        } else {
            return mCursor.getCount();
        }
    }







    /**
     * Swap in a new Cursor, returning the old Cursor.
     * The returned old Cursor is <em>not</em> closed.
     *
     * @param newCursor The new cursor to be used
     * @return Returns the previously set Cursor, or null if there wasnt one.
     * If the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned
     *
     *
     * Should be called whenever the cursor that the adapter is using is changed. We'll see it being
     * called in main activity fragment when we provide a valid cursor for the first time and again
     * when the loader is reset.
     *
     * The method returns the previous cursor, which can be useful if the owner of that cursor needs
     * to close it. We dont have to worry about that because our cursor loader class takes care of it.
     */
    Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if(newCursor != null) {
            //notify the observers(such as a recyclerView) about the new cursor
            notifyDataSetChanged();
        } else {
            //notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;

    }








    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "TaskViewHolder";

        TextView name = null;
        TextView description = null;
        ImageButton editButton = null;
        ImageButton deleteButton = null;


        public TaskViewHolder(View itemView) {
            super(itemView);
//            Log.d(TAG, "TaskViewHolder: starts");
            this.name = (TextView) itemView.findViewById(R.id.tli_name);
            this.description = (TextView) itemView.findViewById(R.id.tli_description);
            this.editButton = (ImageButton) itemView.findViewById(R.id.tli_edit);
            this.deleteButton = (ImageButton) itemView.findViewById(R.id.tli_delete);
        }
    }
}
