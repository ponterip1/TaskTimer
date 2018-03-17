package com.peterponterio.tasktimer;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivityFragment";

    public static final int LOADER_ID = 0;



    private CursorRecyclerViewAdapter mAdapter; //add adapter reference





    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: starts");
    }









    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: starts");
        super.onActivityCreated(savedInstanceState);
        /*
             get a reference to the loader manager by calling the fragments getLoaderManager method, and
             then we use that to call initLoader. This is where our LOADER_ID comes into play because
             we have to tell the manager which loader we're initializing. The loader manager passes that
             ID in as an argument to all our methods that it calls so that we can make the methods
             respond differently depending on which loader theyre getting called for

             The second parameter will always be null when using Androids cursor loader class because
              that doesnt accept any other parameters. But if you've extended or created your own loader,
              then you may need to pass extra arguments in a bundle

              The third parameter is a loader manager.loader callbacks object, so this is where we tell
              the manager which object will be handling the onCreate, the onLoadFinished and the OnLoaderReset
              calls. Youd normally use the activity or fragment thats initializing the loader and thats
              why we've passes 'this' for our argument. So our fragment implements LoaderManager.LoaderCallBacks
              so 'this' is fine
         */
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }












    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");


        /*
            we can use findViewById to get a reference to the recyclerView in our layout. Store
            the view in a variable so we can call findViewById

            Create a new cursor RecyclerView Adapter and link that to our recycler view
         */
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //no data yet, initializing with null which will cause it to return a view with instructions
        mAdapter = new CursorRecyclerViewAdapter(null); 
        recyclerView.setAdapter(mAdapter);

        Log.d(TAG, "onCreateView: returning");

        return view;
    }














    /*
        loader manager will call these methods when it wants to notify our fragment of important events
        about the loader. The first one is onCreateLoader, which will instantiate and return a new loader
        for the given ID. The cursor loader will basically just be running a query for us but it does
        it on the background thread, but essentially its just fetching data from the database


        The loader manager will be ready for a fragment to define the loader that it wants to use. So
        it calls our fragments onCreateLoader method and we set up the parameters for a new cursor loader
        object and then return it. Now the loader manager then sets the cursor loader away, fetching the
        data on a background thread. When the cursor loader has retrieved all the data, it lets the loader
        manager know and the loader manager calls out the onLoadFinished method, passing the cursor to
        it so that we've got the data.
     */

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: starts with id " + id);
        /*
            create a string array to hold the columns to hold the columns that we want to fetch from the
            database. we also specify sortOrder and we're sorting on the sort order column and then
            alphabetically by name for tasks of the same sort order
         */
        String[] projection = {TasksContract.Columns._ID, TasksContract.Columns.TASKS_NAME,
                                TasksContract.Columns.TASKS_DESCRIPTION, TasksContract.Columns.TASKS_SORTORDER};
        String sortOrder = TasksContract.Columns.TASKS_SORTORDER + "," + TasksContract.Columns.TASKS_NAME;

        switch(id) {
            case LOADER_ID:
                /*
                    the first we pass to the cursor loader constructor is a context and we could pass
                    in the activity that this fragment is attached to. Next we give it the uri of our
                    tasks table and a list of the columns that we want. Now we want to display all the
                    records so the next two parameters, selection and selectionArgs are null. Then we
                    provide the sortOrder columns.

                    if we had additional loaders, we would just add additional cases to the switch
                    statement
                 */
                return new CursorLoader(getActivity(),
                        TasksContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);
            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
    }











    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Entering onLoadFinished");

        /*
            calling the swapCursor method and were retrieving the number of records from the adapter
         */
        mAdapter.swapCursor(data);
        int count = mAdapter.getItemCount();

        Log.d(TAG, "onLoadFinished: count is " + count);

    }









    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");

        //adapter no longer holds a reference to the cursor. loader is free to close cursor now as well
        mAdapter.swapCursor(null);

    }
}
