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

    public CursorRecyclerViewAdapter(Cursor cursor) {
        Log.d(TAG, "CursorRecyclerViewAdapter: Constructor called");
        mCursor = cursor;
    }


    /*
        called by the recycler view when it needs a new view to display

        will inflate a view from the task_list_items layout and then return the view
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
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
        Log.d(TAG, "onBindViewHolder: starts");



        //if there wasnt any records, any data, or the cursor was null
        if((mCursor == null) || (mCursor.getCount() == 0)) {
            Log.d(TAG, "onBindViewHolder: providing instructions");
            holder.name.setText(R.string.instructions_heading);
            holder.description.setText(R.string.instructions);
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        } else { //if the cursor isnt null, display the data
            if(!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldnt move cursor to position " + position);
            }
            /*
                as long as we've got a valid cursor that contains atleast one row, the code retrieves
                the value from the cursor and uses them to set the text in the textView widgets
             */
            holder.name.setText(mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)));
            holder.description.setText(mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_DESCRIPTION)));
            holder.editButton.setVisibility(View.VISIBLE); // TODO add onClick Listener
            holder.deleteButton.setVisibility(View.VISIBLE); //TODO add onClick Listener
        }



    }







    /*
        recyclerView uses this so it knows how many items there are to display. If there are no items,
        we will be sending back a view that displays the instructions. So in this case we dont want to
         tell the recyclerView that there are no records. we'll always return atleast 1

     */
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts");

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
            Log.d(TAG, "TaskViewHolder: starts");
            this.name = (TextView) itemView.findViewById(R.id.tli_name);
            this.description = (TextView) itemView.findViewById(R.id.tli_description);
            this.editButton = (ImageButton) itemView.findViewById(R.id.tli_edit);
            this.deleteButton = (ImageButton) itemView.findViewById(R.id.tli_delete);
        }
    }
}
