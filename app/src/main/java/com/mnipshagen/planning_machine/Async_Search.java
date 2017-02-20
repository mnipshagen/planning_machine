package com.mnipshagen.planning_machine;

import android.app.Activity;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * A background task to query the database of all courses
 */

public class Async_Search extends AsyncTask<String[], Void, Cursor> {
    // hold a reference to the activity from which it was started
    // weak, so it may be collected in case the activity is leaving the active view while searching
    private WeakReference<Activity> mWeakActivity;

    public Async_Search (Activity activity) {
        this.mWeakActivity = new WeakReference<Activity>(activity);
    }

    @Override
    protected Cursor doInBackground(String[]... params) {
        // get the database
        SQLiteDatabase db = SQL_Database.getInstance(null).getWritableDatabase();
        // we gave over the columns to collectt as first parameter
        String[] columns = params[0];
        // and the second parameter was the selection string inside an array
        String selection = params[1][0];
        // return the database query
        return db.query(SQL_Database.COURSE_TABLE_NAME,
                        columns,
                        selection,
                        null,
                        null,
                        null,
                        SQL_Database.COURSE_COLUMN_YEAR + " DESC");
    }

    @Override
    protected void onPostExecute(Cursor results) {
        // collect the activity from the weak reference
        Activity activity = mWeakActivity.get();
        // if the activity is still active call the onSearchCompleted method
        if (activity != null) {
            ((Activity_Search_Card)activity).onSearchCompleted(results);
        }
        // dump the cursor for debug reasons
        // Log.v("Async result", DatabaseUtils.dumpCursorToString(results));
    }
}
