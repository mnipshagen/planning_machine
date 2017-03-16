package com.mnipshagen.planning_machine.Dialogs;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.mnipshagen.planning_machine.Activity_Module;
import com.mnipshagen.planning_machine.DataProviding.DataProvider;
import com.mnipshagen.planning_machine.DataProviding.SQL_Database;
import com.mnipshagen.planning_machine.ModuleTools;

/**
 * Created by nipsh on 15/03/2017.
 */

public class AddCourseDialog extends DialogFragment {

    private String[] modules;
    private long rowID;
    private int selected;

    public AddCourseDialog() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if(args == null) throw new IllegalArgumentException("Did not provide a module list & action type!");
        boolean go;
        try {
            modules = args.getStringArray("modules");
            go = args.getBoolean("go");
            rowID = args.getLong("rowID");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Failed to provide necessary data");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add to which Module?")
                .setSingleChoiceItems(modules, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selected = which;
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        if (go) {
            builder.setPositiveButton("Add & go", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addCourse();
                    String module = modules[selected];
                    String[] columns = {
                            SQL_Database.MODULE_COLUMN_NAME,
                            SQL_Database.MODULE_COLUMN_ECTS_COMP,
                            SQL_Database.MODULE_COLUMN_ECTS_OPTCOMP,
                            SQL_Database.MODULE_COLUMN_GRADE,
                            SQL_Database.MODULE_COLUMN_SIGNIFICANT
                    };
                    Cursor cursor = getContext().getContentResolver().query(
                            DataProvider.MODULE_DB_URI,
                            columns,
                            SQL_Database.MODULE_COLUMN_CODE + "=" + module,
                            null, null
                    );

                    cursor.moveToFirst();

                    String name = cursor.getString(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_NAME));
                    int compECTS = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS_COMP));
                    int optcompECTS = cursor.getInt(cursor.getColumnIndexOrThrow(SQL_Database.MODULE_COLUMN_ECTS_OPTCOMP));

                    cursor.close();
                    dismiss();

                    Intent intent = new Intent(getContext(), Activity_Module.class);
                    intent.putExtra("Name",name);
                    intent.putExtra("Module", module);
                    intent.putExtra("compECTS", compECTS);
                    intent.putExtra("optcompECTS", optcompECTS);
                    getContext().startActivity(intent);
                }
            });
        } else {
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addCourse();
                    dismiss();
                }
            });
        }

        return builder.create();
    }

    private void addCourse() {
        String module = ModuleTools.getModuleCode(modules[selected]);

        String[] columns = {
                SQL_Database.COURSES_COLUMN_COURSE_ID,
                SQL_Database.COURSE_COLUMN_COURSE,
                SQL_Database.COURSE_COLUMN_COURSE_DESC,
                SQL_Database.COURSE_COLUMN_ECTS,
                SQL_Database.COURSE_COLUMN_TERM,
                SQL_Database.COURSE_COLUMN_YEAR,
                SQL_Database.COURSE_COLUMN_CODE,
                SQL_Database.COURSE_COLUMN_TYPE,
                SQL_Database.COURSE_COLUMN_INFIELD_TYPE,
                SQL_Database.COURSE_COLUMN_TEACHERS,
                SQL_Database.COURSE_COLUMN_TEACHERS_STR,
                SQL_Database.COURSE_COLUMN_FIELDS,
                SQL_Database.COURSE_COLUMN_FIELDS_STR,
                SQL_Database.COURSE_COLUMN_SINGLE_FIELD
        };

        Cursor c = getContext().getContentResolver().query(
                DataProvider.COURSE_DB_URI,
                columns,
                SQL_Database.COURSE_COLUMN_ID + "=" + rowID,
                null, null
        );
        ContentValues cv = new ContentValues();
        cv.put(SQL_Database.COURSES_COLUMN_MODULE, module);
        for (String s : columns) {
            switch (c.getType(c.getColumnIndexOrThrow(s))) {
                // type null
                case 0:
                    break;
                // type int
                case 1:
                    cv.put(s, c.getInt(c.getColumnIndexOrThrow(s)));
                    break;
                // type float
                case 2:
                    cv.put(s, c.getFloat(c.getColumnIndexOrThrow(s)));
                    break;
                //type String
                case 3:
                    cv.put(s, c.getString(c.getColumnIndexOrThrow(s)));
                    break;
                // blob...
                default:
                    cv.put(s, c.getBlob(c.getColumnIndexOrThrow(s)));
            }
        }
        getContext().getContentResolver().insert(DataProvider.COURSES_DB_URI, cv);
        c.close();
    }
}
