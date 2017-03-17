package com.mnipshagen.planning_machine.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Whenever the app is started, show this instead of the boring white screen
 */

public class Activity_Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO make it pretty
        Intent intent = new Intent(this, Activity_Overview.class);
        startActivity(intent);
        finish();
    }
}