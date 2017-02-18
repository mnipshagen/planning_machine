package com.mnipshagen.planning_machine;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * The Activity corresponding to the 'About' drawer option.
 * Of no use so far. Only for test & debug purposes.
 */

public class Activity_About extends Activity_Base {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setActionBarTitle("About aka debug for now");

        EditText typer = (EditText) findViewById(R.id.debugedit);

        typer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                TextView outer = (TextView) findViewById(R.id.debugfield);
                outer.setText(outer.getText() + "\n It was pressed Key: " + keyCode);

                return true;
            }
        });
    }
}
