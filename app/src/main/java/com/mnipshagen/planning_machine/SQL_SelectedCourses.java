package com.mnipshagen.planning_machine;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nipsh on 08/02/2017.
 */

public class SQL_SelectedCourses extends SQLiteOpenHelper {

    final static int DB_VERSION = 1;
    final static String DB_NAME = "selected_courses.s3db";
    Context context;

    public SQL_SelectedCourses(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // Store the context for later use
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        SQL_Tools.executeSQLScript(database, DB_NAME, context);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int j) {

    }
}
