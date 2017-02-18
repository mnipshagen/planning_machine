package com.mnipshagen.planning_machine;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

/**
 * Dialog fragment displayed on a long touch on the search
 */

public class Dialog_AddCourseToModule extends DialogFragment {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // collect the arguments
        Bundle args = this.getArguments();
        // collect the string with all modules
        String moduleList = args.getString("ModuleList");
        if (moduleList == null) {
            // if there were no modules given over, cancel the dialog
            Dialog_AddCourseToModule.this.getDialog().cancel();
        }
        // split the modules on each comma so we have each one as a single string
        final String[] modules = moduleList.split(",");
        // delete the hardcoded "in" from the first module
        modules[0] = modules[0].substring(3);
        // collect the position from the recyclerview
        final int pos = args.getInt("Position");
        // and now let us build a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // the title displayed
        // TODO make into resource string
        builder.setTitle("Add to which Module?")
                // set the modules as a list to select one from
                .setItems(modules,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // when one was selected, write it into the database
                        // get database
                        SQLiteDatabase db = SQL_Database.getInstance(getActivity()).getWritableDatabase();
                        // and get the adapter from the recycler view to collect the sql id from it
                        Adapter_Search adapter
                                = (Adapter_Search) ((RecyclerView)getActivity().findViewById(R.id.searchRecycler)).getAdapter();
                        // collect the "_id"
                        long rowID = adapter.getItemId(pos);
                        // all the columns we need to copy
                        String columns =    SQL_Database.COURSES_COLUMN_COURSE_ID + "," +
                                            SQL_Database.COURSE_COLUMN_COURSE + "," +
                                            SQL_Database.COURSE_COLUMN_COURSE_DESC + "," +
                                            SQL_Database.COURSE_COLUMN_ECTS + "," +
                                            SQL_Database.COURSE_COLUMN_TERM + "," +
                                            SQL_Database.COURSE_COLUMN_YEAR + "," +
                                            SQL_Database.COURSE_COLUMN_CODE + "," +
                                            SQL_Database.COURSE_COLUMN_TYPE + "," +
                                            SQL_Database.COURSE_COLUMN_INFIELD_TYPE + "," +
                                            SQL_Database.COURSE_COLUMN_TEACHERS + "," +
                                            SQL_Database.COURSE_COLUMN_TEACHERS_STR + "," +
                                            SQL_Database.COURSE_COLUMN_FIELDS + "," +
                                            SQL_Database.COURSE_COLUMN_FIELDS_STR + "," +
                                            SQL_Database.COURSE_COLUMN_SINGLE_FIELD;
                        // we need to encode the module name to its corresponding code
                        // using contains as there might be blanks and (PM) inside module name
                        String module;
                        if (modules[which].contains("ficial")) {
                            module = "KI";
                        }else if (modules[which].contains("roinfo")) {
                            module = "NI"; 
                        }else if (modules[which].contains("psych")) {
                            module = "KNP"; 
                        }else if (modules[which].contains("lingu")) {
                            module = "CL"; 
                        }else if (modules[which].contains("bio")) {
                            module = "NW"; 
                        }else if (modules[which].contains("sophy")) {
                            module = "PHIL"; 
                        }else if (modules[which].contains("ompute")) {
                            module = "INF"; 
                        }else if (modules[which].contains("athem")) {
                            module = "MAT"; 
                        }else {
                            module = "OPEN"; 
                        }

                        db.execSQL("INSERT INTO " + SQL_Database.COURSES_TABLE_NAME +
                                "(" + columns + ") SELECT " + columns + " FROM " +
                                SQL_Database.COURSE_TABLE_NAME + " WHERE _id = " + rowID);
                        db.execSQL("UPDATE " + SQL_Database.COURSES_TABLE_NAME + " SET " +
                                SQL_Database.COURSES_COLUMN_MODULE + "= '" + module +
                                "' WHERE _id = (SELECT MAX(_id) FROM courses)");
                    }
                });

        return builder.create();
    }
}
