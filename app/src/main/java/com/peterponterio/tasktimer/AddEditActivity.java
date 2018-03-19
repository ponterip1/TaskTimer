package com.peterponterio.tasktimer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class AddEditActivity extends AppCompatActivity {
    private static final String TAG = "AddEditActivity";

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

}
