package com.mnipshagen.planning_machine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by nipsh on 11/02/2017.
 */

public class Activity_Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, Activity_Main.class);
        startActivity(intent);
        finish();
    }
}