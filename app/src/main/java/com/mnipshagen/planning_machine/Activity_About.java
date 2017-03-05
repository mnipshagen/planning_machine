package com.mnipshagen.planning_machine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
        final TextView outer = (TextView) findViewById(R.id.debugfield);
        SQLiteDatabase db = SQL_Database.getInstance(this).getReadableDatabase();
        outer.setText(getTableAsString(db, SQL_Database.COURSES_TABLE_NAME));

        EditText typer = (EditText) findViewById(R.id.debugedit);
        typer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                outer.setText(outer.getText() + "\n It was pressed Key: " + keyCode);

                return true;
            }
        });
    }

    public String getTableAsString(SQLiteDatabase db, String tableName) {
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }
}
